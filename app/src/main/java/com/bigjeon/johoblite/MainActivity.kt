package com.bigjeon.johoblite

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.commitNow
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bigjeon.johoblite.analysis.CadFragment
import com.bigjeon.johoblite.analysis.StitchingInspectFragment
import com.bigjeon.johoblite.data.*
import com.bigjeon.johoblite.databinding.ActivityMainBinding
import com.bigjeon.johoblite.dialog.TutorialDialog
import com.bigjeon.johoblite.home.FacilityFragment
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.pageadapter.MainViewPagerAdapter
import com.bigjeon.johoblite.rcvadapter.KeyPlanRcvAdapter
import com.bigjeon.johoblite.rcvadapter.PlaneExpandableListAdapter
import com.bigjeon.johoblite.report.CadReportFragment
import com.bigjeon.johoblite.report.HistoryInquiryFragment
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.service.SocketFactory
import com.bigjeon.johoblite.upload.UploadInspectionFragment
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    
    companion object{
        private val facilitySpinnerList = arrayListOf("시설물 선택")
        private val facilityList = arrayListOf(Facility(0, "NONE", "NONE"))
        private val projectSpinnerList = arrayListOf("프로젝트 선택")
        private val projectList = arrayListOf(Project(0, "NONE", "NONE"))
        private val directionSpinnerList = arrayListOf("구역 선택")
        private val directionList = arrayListOf(Direction("NONE", "NONE"))
        private val keyPlanList = arrayListOf<KeyPlan>()
        private val memberList = mutableListOf<Member>()
        private val planeList = mutableListOf<MutableList<Plane>>()
    }
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: MainViewModel
    private lateinit var sharedPreference: SharedPreferences
    
    //retrofit2
    private lateinit var serviceClient: OcrFactory
    private lateinit var notificationSocket: Socket
    //Adapter
    private lateinit var facilityArrayAdapter : ArrayAdapter<String>
    private lateinit var projectArrayAdapter : ArrayAdapter<String>
    private lateinit var directionArrayAdapter : ArrayAdapter<String>
    private lateinit var keyPlanAdapter : KeyPlanRcvAdapter
    private lateinit var pLaneAdapter : PlaneExpandableListAdapter
    
    private var navigationState = false
    private var onBackKeyPressed = 0L
    
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        when(sharedPreference.getInt("USER_THEME", 0)){
            0 -> setTheme(R.style.Theme_Komapperlite_default)
            1 -> setTheme(R.style.Theme_Komapperlite_orange)
            2 -> setTheme(R.style.Theme_Komapperlite_green)
            3 -> setTheme(R.style.Theme_Komapperlite_purple)
            4 -> setTheme(R.style.Theme_Komapperlite_brown)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        /*binding.userProfileImage.setOnClickListener {
            val userPageIntent = Intent(this, UserActivity::class.java)
            startActivity(userPageIntent)
        }*/
        binding.mainUserNameTv.text = sharedPreference.getString("USER_ID", "NONE") + "\n(" + sharedPreference.getString("USER_NAME", "NONE") + ")"
        
        model = ViewModelProvider(this)[MainViewModel::class.java]
        serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
        notificationSocket = SocketFactory.get()
        notificationSocket.connect()
        
        //navigation Drawer
        binding.toolbarControlBtn.setOnClickListener {
            if (navigationState){
                binding.mainDrawerLayout.close()
            }else{
                binding.mainDrawerLayout.open()
            }
        }
        //초기화
        initNavigationMenu()
        initTitleTab()
        facilitySpinnerAdapter()
        projectSpinnerAdapter()
        directionSpinnerAdapter()
        initKeyPlanList()
        initPlaneList(memberList, planeList)
        getCSTFacility()
        tutorialDialog()
        
        //Observer
        model.facilityState.observe(this){ source ->
            when(source.Name){
                "NONE"-> {
                    binding.customToolBar.toolbarProjectSpinner.setSelection(0)
                    binding.customToolBar.toolbarDirectionSpinner.setSelection(0)
                    binding.customToolBar.toolbarProjectSpinner.isEnabled = false
                    binding.customToolBar.toolbarDirectionSpinner.isEnabled = false
                }
                else -> {
                    getProjectDirectionList()
                    binding.customToolBar.toolbarProjectSpinner.isEnabled = true
                    binding.customToolBar.toolbarDirectionSpinner.isEnabled = true
                }
            }
        }
        model.projectState.observe(this){ source ->
            when(source.projectCode){
                "NONE" -> {
                    keyPlanAdapter.clearList()
                    pLaneAdapter.clearList()
                    if (model.planeSectionState.value!!.code != "NONE"){
                        model.setPlaneSection(Plane("NONE", "NONE"))
                    }
                }
                else -> {
                    if (model.directionState.value!!.Code != "NONE"){
                        getKeyPlanList()
                    }
                }
            }
        }
        model.directionState.observe(this){ source ->
            when(source.Code){
                "NONE" -> {
                    keyPlanAdapter.clearList()
                    pLaneAdapter.clearList()
                    if (model.planeSectionState.value!!.code != "NONE"){
                        model.setPlaneSection(Plane("NONE", "NONE"))
                    }
                }
                else -> {
                    if ( model.projectState.value!!.projectCode != "NONE"){
                        keyPlanAdapter.clearList()
                        pLaneAdapter.clearList()
                        getKeyPlanList()
                    }
                }
            }
        }
        
        model.keyPlanState.observe(this){ source ->
            if (source != "NONE"){
                getProjectPlaneList()
            }
        }
        
        model.planeState.observe(this){ source ->
            if (source == "NONE"){
                model.setPlaneSection(Plane("NONE", "NONE"))
            }
        }
        
        model.planeSectionState.observe(this) { source ->
            Log.d("PLANESTATE", source.toString())
            if (source.code == "NONE"){
                binding.subTitleViewPager2.isUserInputEnabled = false
                val tabStrip = binding.titleTabLayout.getChildAt(0) as LinearLayout
                for (i in 1 until tabStrip.childCount){
                    tabStrip.getChildAt(i).setOnTouchListener{ _, _ ->
                        if (i >= 1){
                            Toast.makeText(applicationContext, "작업할 구역을 설정해주세요.", Toast.LENGTH_SHORT).show()
                            binding.mainDrawerLayout.open()
                        }
                        true}
                }
            }else{
                binding.subTitleViewPager2.isUserInputEnabled = true
                val tabStrip = binding.titleTabLayout.getChildAt(0) as LinearLayout
                for (i in 1 until tabStrip.childCount){
                    tabStrip.getChildAt(i).setOnTouchListener{ _, _ -> false}
                }
            }
        }
        model.titleState.observe(this){ source ->
            if (source == "Home"){
                binding.customToolBar.toolbarFacilitySpinner.isEnabled = true
                binding.customToolBar.toolbarProjectSpinner.isEnabled = true
            }else{
                binding.customToolBar.toolbarFacilitySpinner.isEnabled = false
                binding.customToolBar.toolbarProjectSpinner.isEnabled = false
            }
        }
        
        model.subTitleState.observe(this){ source ->
            model.setFragment(source)
        }
        
        model.fragmentState.observe(this){ source ->
            changeFragment(source)
        }
        
    }
    private fun initNavigationMenu(){
        val drawerLayout = binding.mainDrawerLayout
        val navigation = binding.navigationView
        navigation.setNavigationItemSelectedListener(this)
        binding.customToolBar.closeBtnContainer.setOnClickListener {
            drawerLayout.close()
        }
        //LOCK Drawer Scrolling
        binding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.mainDrawerLayout.open()
    }
    //socket 통신(Notification)
    var onNotification = Emitter.Listener { args ->
        val obj = JSONObject(args[0].toString())
        Log.d("main activity", obj.toString())
        Thread {
            runOnUiThread(Runnable {
                kotlin.run {
            
                }
            })
        }.start()
    }
    
    private fun getCSTFacility(){
        facilityList.add(Facility(0, "조압수조", "FAC1"))
        facilityArrayAdapter.add("조압수조")
        /*CoroutineScope(Dispatchers.IO).launch {
            serviceClient.getCstFacilityList().enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.get("result").asString.equals("true")){
                            for (i in response.body()?.getAsJsonArray("facilList")!!){
                                facilityList.add(Facility(i.asJsonObject.get("ID").asInt, i.asJsonObject.get("NAME").asString, i.asJsonObject.get("CODE").asString))
                                facilityArrayAdapter.add(i.asJsonObject.get("NAME").asString)
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                }
            })
        }*/
    }
    private fun getProjectDirectionList() {
        projectList.clear()
        projectArrayAdapter.clear()
        directionList.clear()
        directionArrayAdapter.clear()
        projectList.add(Project(0, "NONE", "NONE"))
        projectArrayAdapter.add("프로젝트 선택")
        directionList.add(Direction("NONE", "NONE"))
        directionArrayAdapter.add("구역 선택")
        projectList.add(Project(0, "2022 안전 진단", "PRC1"))
        projectArrayAdapter.add("2022 안전 진단")
        directionList.add(Direction("인천계통", "DIR1"))
        directionArrayAdapter.add("인천계통")
        directionList.add(Direction("평택계통", "DIR2"))
        directionArrayAdapter.add("평택계통")
        /*CoroutineScope(Dispatchers.IO).launch {
            serviceClient.getCstProjectList(model.facilityState.value!!.Code).enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.get("result").asString.equals("true")){
                            for (i in response.body()?.getAsJsonArray("projectList")!!){
                                projectList.add(Project(i.asJsonObject.get("ID").asInt, i.asJsonObject.get("NAME").asString, i.asJsonObject.get("RANGE_RULE").asString))
                                projectArrayAdapter.add(i.asJsonObject.get("NAME").asString)
                            }
                            for (i in response.body()?.get("directionInfo")!!.asJsonArray){
                                projectList.add(Project(i.asJsonObject.get("ID").asInt, i.asJsonObject.get("NAME").asString, i.asJsonObject.get("RANGE_RULE").asString))
                                projectArrayAdapter.add(i.asJsonObject.get("NAME").asString)
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                }
            })
        }*/
    }
    private fun getKeyPlanList() {
        keyPlanList.clear()
        keyPlanAdapter.clearList()
        keyPlanList.add(KeyPlan("P1"))
        keyPlanAdapter.changeList(keyPlanList)
        /*CoroutineScope(Dispatchers.IO).launch {
            serviceClient.getCstKeyPlanList(model.facilityState.value!!.Code + File.separator + model.directionState.value!!.Code).enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.get("result").asString.equals("true")){
                            for (i in response.body()?.getAsJsonArray("range")!!){
                                keyPlanList.add(KeyPlan(i.asString))
                                if (i.asString.contains("P")){
                                    keyPlanList.add(KeyPlan(i.asString.replace("P", "S")))
                                }
                            }
                            keyPlanList.removeAt(keyPlanList.size - 1)
                            keyPlanAdapter.changeList(keyPlanList)
                        }
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                }
            })
        }*/
    }
    private fun getProjectPlaneList() {
        pLaneAdapter.clearList()
        if (model.directionState.value?.Name == "인천계통"){
            memberList.add(Member("MBR010", "조압수조"))
            val list = mutableListOf<Plane>()
            list.add(Plane("팔당", "PL1"))
            planeList.add(list)
            pLaneAdapter.setList(memberList, planeList)
        }else if(model.directionState.value?.Name == "평택계통"){
            memberList.add(Member("MBR010", "조압수조"))
            val list = mutableListOf<Plane>()
            list.add(Plane("성남", "PL1"))
            planeList.add(list)
            pLaneAdapter.setList(memberList, planeList)
        }
       
        /*CoroutineScope(Dispatchers.IO).launch {
            serviceClient.getCstPlaneList(model.facilityState.value!!.Code + File.separator + model.directionState.value!!.Code + File.separator + model.keyPlanState.value).enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.get("result").asString.equals("true")){
                            for (i in response.body()!!.getAsJsonArray("memberList").asJsonArray){
                                memberList.add(Member(i.asJsonObject.get("code").asString, i.asJsonObject.get("name").asString))
                                val list = mutableListOf<Plane>()
                                for (j in i.asJsonObject.get("planeList").asJsonArray){
                                    list.add(Plane(j.asJsonObject.get("name").asString, j.asJsonObject.get("code").asString))
                                }
                                planeList.add(list)
                            }
                            pLaneAdapter.setList(memberList, planeList)
                        }
                    }
                }
                
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                }
            })
        }*/
    }
    private fun initTitleTab(){
        val list = resources.getStringArray(R.array.title_array)
        //Viewpager2
        binding.subTitleViewPager2.offscreenPageLimit = 1
        binding.subTitleViewPager2.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
        binding.subTitleViewPager2.adapter = MainViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.titleTabLayout.setSelectedTabIndicatorColor(MaterialColors.getColor(this, R.attr.default_color, Color.BLACK))
        TabLayoutMediator(binding.titleTabLayout, binding.subTitleViewPager2) { tab, position ->
            tab.text = list[position]
        }.attach()
        
        binding.subTitleViewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                    when(position){
                        0 -> {
                            model.setTitle(resources.getString(R.string.Home))
                            model.setSubTitle(resources.getString(R.string.Facility_Home))
                        }
                        1 -> {
                            model.setTitle(resources.getString(R.string.Upload))
                            model.setSubTitle(resources.getString(R.string.Inspection_Upload))
                        }
                        2 -> {
                            model.setTitle(resources.getString(R.string.Analysis))
                            model.setSubTitle(resources.getString(R.string.Stitching_Inspection_Analysis))
                        }
                        3 -> {
                            model.setTitle(resources.getString(R.string.Report))
                            model.setSubTitle(resources.getString(R.string.Original_Report))
                        }
                    }
                super.onPageSelected(position)
            }
        })
    }
    
    private fun facilitySpinnerAdapter() {
        facilityArrayAdapter = ArrayAdapter(this, R.layout.spinnertv, facilitySpinnerList)
        facilityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.customToolBar.toolbarFacilitySpinner.adapter = facilityArrayAdapter
        binding.customToolBar.toolbarFacilitySpinner.dropDownVerticalOffset =
            dipToPixels(40f).toInt()
        binding.customToolBar.toolbarFacilitySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    model.setFacility(facilityList[position])
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }
    private fun projectSpinnerAdapter() {
        projectArrayAdapter = ArrayAdapter(this, R.layout.spinnertv, projectSpinnerList)
        projectArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.customToolBar.toolbarProjectSpinner.adapter = projectArrayAdapter
        binding.customToolBar.toolbarProjectSpinner.dropDownVerticalOffset =
            dipToPixels(40f).toInt()
        binding.customToolBar.toolbarProjectSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    model.setProject(projectList[position])
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        binding.customToolBar.toolbarProjectSpinner.isEnabled = true
    }
    
    private fun directionSpinnerAdapter() {
        directionArrayAdapter = ArrayAdapter(this, R.layout.spinnertv, directionSpinnerList)
        directionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.customToolBar.toolbarDirectionSpinner.adapter = directionArrayAdapter
        binding.customToolBar.toolbarDirectionSpinner.dropDownVerticalOffset =
            dipToPixels(40f).toInt()
        binding.customToolBar.toolbarDirectionSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    model.setDirection(directionList[position])
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        binding.customToolBar.toolbarDirectionSpinner.isEnabled = true

    }
    
    private fun initKeyPlanList(){
        val lm = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        keyPlanAdapter = KeyPlanRcvAdapter()
        binding.customToolBar.keyPlanRcv.adapter = keyPlanAdapter
        binding.customToolBar.keyPlanRcv.setHasFixedSize(false)
        binding.customToolBar.keyPlanRcv.layoutManager = lm
        keyPlanAdapter.setOnItemClickListener(object: KeyPlanRcvAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: String) {
                //!model.keyPlanState.value.equals(data) &&
                        if (!data.contains("S")){
                    model.setKeyPlan(data)
                    keyPlanAdapter.setShowingPosition(data)
                }
            }
        })
    }
    private fun initPlaneList(parent : MutableList<Member>, child: MutableList<MutableList<Plane>>) {
        pLaneAdapter = PlaneExpandableListAdapter(this, parent, child)
        val planeListView = binding.toolbarPlaneExpandableListView.findViewById<ExpandableListView>(R.id.toolbarPlaneExpandableListView)
        val planeListAdapter = pLaneAdapter
        planeListView.setAdapter(planeListAdapter)
        planeListView.setOnGroupClickListener { _, _, _, _ ->
            planeListAdapter.changeSelectedParentPosition()
            false
        }
        planeListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            planeListAdapter.changeSelectedChildPosition(groupPosition, childPosition)
            planeListAdapter.getChild(groupPosition, childPosition)
            model.setPlane(parent[groupPosition].code)
            model.setPlaneSection(child[groupPosition][childPosition])
            false
        }
    }
    @Synchronized
    private fun changeFragment(state: String){
        model.setLayerSelectedPosition(-1)
        supportFragmentManager.commitNow {
            when(state){
                "NONE" -> {
                    Toast.makeText(applicationContext, "개발 진행중인 기능입니다.", Toast.LENGTH_SHORT).show()
                }
                resources.getString(R.string.Facility_Home) -> {
                    replace(R.id.mainFrameLayout, FacilityFragment())
                }
                /*resources.getString(R.string.Progress_Home) -> {
					supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout, ProgressFragment()).commit()
				}*/
                resources.getString(R.string.Inspection_Upload) -> {
                    replace(R.id.mainFrameLayout, UploadInspectionFragment())
                }
                /*resources.getString(R.string.Remuneration_Investigation_Analysis) -> {
					supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout, CrackDataInputFragment()).commit()
				}*/
                resources.getString(R.string.cad_Analysis) -> {
                    replace(R.id.mainFrameLayout, CadFragment())
                }
                resources.getString(R.string.Stitching_Inspection_Analysis) -> {
                    replace(R.id.mainFrameLayout, StitchingInspectFragment())
                }
                /*resources.getString(R.string.AI_Inspection_Analysis) -> {
					supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout, DetectionInspectFragment()).commit()
				}*/
                /*resources.getString(R.string.Digital_Report) -> {
                    replace(R.id.mainFrameLayout, ReportDigitalReportFragment())
                }*/
                resources.getString(R.string.History_Report) -> {
                    replace(R.id.mainFrameLayout, HistoryInquiryFragment())
                }
                resources.getString(R.string.Original_Report) -> {
                    replace(R.id.mainFrameLayout, CadReportFragment())
                }
            }
        }
        
    }
    
    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }
    
    
    private fun tutorialDialog(){
        val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        
        val dialog = TutorialDialog(size.x, size.y)
        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager, "Dialog")
    }
    
    override fun onPause() {
        super.onPause()
        when(sharedPreference.getInt("USER_THEME", 0)){
            0 -> setTheme(R.style.Theme_Komapperlite_default)
            1 -> setTheme(R.style.Theme_Komapperlite_orange)
            2 -> setTheme(R.style.Theme_Komapperlite_green)
            3 -> setTheme(R.style.Theme_Komapperlite_purple)
            4 -> setTheme(R.style.Theme_Komapperlite_brown)
        }
    }
    
    override fun onBackPressed() {
        if (System.currentTimeMillis() > onBackKeyPressed + 2000L){
            onBackKeyPressed = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (System.currentTimeMillis() <= onBackKeyPressed + 2000L){
            moveTaskToBack(true) // 태스크를 백그라운드로 이동
            finishAndRemoveTask() // 액티비티 종료 + 태스크 리스트에서 지우기
            exitProcess(0)
        }
        super.onBackPressed()
    }
    
    fun setSelectedPosition(pos : Int){
        model.setLayerSelectedPosition(pos)
    }
    fun addCrackCount(){
        model.addCrackCount()
    }
    fun setCadSelectedPosition(pos : Int){
        model.setLayerSelectedPosition(pos)
    }
    fun addCadCrackCount(){
        model.addCrackCount()
    }
    fun checkAccessibleFacility(name: String){
        for (i in facilityList.indices){
            if (facilityList[i].Name == name){
                binding.customToolBar.toolbarFacilitySpinner.setSelection(i)
            }
        }
    }
}