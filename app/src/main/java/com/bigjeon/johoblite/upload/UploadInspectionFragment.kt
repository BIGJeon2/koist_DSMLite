package com.bigjeon.johoblite.upload

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.anim.ToggleAnimation
import com.bigjeon.johoblite.data.DroneImageItem
import com.bigjeon.johoblite.data.Section
import com.bigjeon.johoblite.databinding.FragmentUploadInspectionBinding
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.rcvadapter.ScaleRcvAdapter
import com.bigjeon.johoblite.rcvadapter.UploadDroneImageRcvAdapter
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class UploadInspectionFragment : Fragment() {
	
	private lateinit var binding: FragmentUploadInspectionBinding
	private val model: MainViewModel by activityViewModels()
	private lateinit var sectionArrayAdapter: ArrayAdapter<String>
	private var sectionSpinnerList = arrayListOf<String>()
	private var sectionList = arrayListOf<Section>()
	private lateinit var droneImageAdapter: UploadDroneImageRcvAdapter
	private lateinit var scaleAdapter: ScaleRcvAdapter
	private var sectionProgressExampleExpanded = true
	private var scaleProgressExpanded = true
	private var scaleProgressExampleExpanded = false
	
	//retrofit2
	private lateinit var serviceClient: OcrFactory
	
	@SuppressLint("ClickableViewAccessibility")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentUploadInspectionBinding.inflate(inflater, container, false)
		
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		
		initUploadDroneImageRcv()
		initScaleRcv()
		initSectionSpinner()
		
		binding.uploadInspectionImageview.maximumScale = 8f

		binding.uploadInspectionImageview.setOnPhotoTapListener { _, _, _ ->
			if (binding.bottomContainer.visibility == View.VISIBLE){
				binding.bottomContainer.slideAnimation(UploadInspectionFragment.SlideDirection.DOWN, UploadInspectionFragment.SlideType.HIDE)
			}else{
				binding.bottomContainer.slideAnimation(UploadInspectionFragment.SlideDirection.UP, UploadInspectionFragment.SlideType.SHOW)
			}
		}
		
		model.planeSectionState.observe(requireActivity()) { source ->
			if (source.code != "NONE"){
				binding.sectionTv.text = source.name
				getSectionList()
			}
		}
		
		//ocClickListener
		binding.scaleExpandBtn.setOnClickListener {
			val show = toggleLayout(!scaleProgressExpanded, binding.scaleExpandBtn, binding.scaleProgressLayout)
			scaleProgressExpanded = show
		}
		binding.uploadStateExampleAlertBtn.setOnClickListener {
			val show = toggleLayout(!scaleProgressExampleExpanded, null, binding.uploadProgressExampleView)
			scaleProgressExampleExpanded = show
		}
		binding.sectionProgressExpandBtn.setOnClickListener {
			val show = toggleLayout(!sectionProgressExampleExpanded, binding.sectionProgressExpandBtn, binding.sectionProgressLayout)
			sectionProgressExampleExpanded = show
		}
		binding.progressExampleCloseBtn.setOnClickListener {
			binding.uploadProgressExampleView.visibility = View.GONE
			scaleProgressExampleExpanded = !scaleProgressExampleExpanded
		}
		
		return binding.root
	}
	private fun initScaleRcv(){
		scaleAdapter = ScaleRcvAdapter(requireContext())
		val lm = LinearLayoutManager(context).also { it.orientation = LinearLayoutManager.HORIZONTAL }
		binding.scaleProgressRcv.adapter = scaleAdapter
		binding.scaleProgressRcv.setHasFixedSize(false)
		binding.scaleProgressRcv.layoutManager = lm
		
	}
	private fun initUploadDroneImageRcv(){
		droneImageAdapter = UploadDroneImageRcvAdapter(requireContext())
		val lm = GridLayoutManager(context, 4).also { it.orientation = LinearLayoutManager.VERTICAL }
		binding.uploadImageRcv.adapter = droneImageAdapter
		binding.uploadImageRcv.setHasFixedSize(false)
		binding.uploadImageRcv.layoutManager = lm
		droneImageAdapter.setOnItemClickListener(object : UploadDroneImageRcvAdapter.OnItemClickListener{
			override fun onItemClick(v: View, data: DroneImageItem, uri: Int, position: Int) {
				binding.uploadImageNameTv.text = data.fileName
				binding.uploadImageUploaderTv.text = data.uploader
				binding.uploadImageDateTv.text = data.date
				val circularProgressDrawable = CircularProgressDrawable(requireContext())
				circularProgressDrawable.strokeWidth = 5f
				circularProgressDrawable.centerRadius = 30f
				circularProgressDrawable.start()
				Glide.with(requireActivity()).load(uri).placeholder(circularProgressDrawable).diskCacheStrategy(
					DiskCacheStrategy.NONE).skipMemoryCache(false).into(binding.uploadInspectionImageview)
				droneImageAdapter.changeShowingPosition(position)
			}
			
		})
	}
	private fun toggleLayout(isExpand: Boolean, view: View?, layoutExpand: LinearLayout?): Boolean {
		if (view != null) {
			ToggleAnimation.toggleArrow(view, isExpand)
		}
		if (isExpand) {
			ToggleAnimation.expand(layoutExpand!!)
		} else {
			ToggleAnimation.collapse(layoutExpand!!)
		}
		return isExpand
	}
	private fun initSectionSpinner(){
		sectionArrayAdapter = ArrayAdapter(requireActivity(), R.layout.spinnertv, sectionSpinnerList)
		sectionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		binding.sectionSpinner.adapter = sectionArrayAdapter
		binding.sectionSpinner.dropDownVerticalOffset = dipToPixels(40f).toInt()
		binding.sectionSpinner.onItemSelectedListener =
			object : AdapterView.OnItemSelectedListener {
				override fun onItemSelected(
					adapterView: AdapterView<*>?,
					view: View?,
					position: Int,
					id: Long
				) {
					model.setSection(sectionList[position].name)
					binding.uploadImageNameTv.text = "이미지를 선택하세요."
					binding.uploadImageUploaderTv.text = "-"
					binding.uploadImageDateTv.text = "-"
					Glide.with(requireActivity()).load(R.color.white).diskCacheStrategy(
						DiskCacheStrategy.NONE).skipMemoryCache(false).into(binding.uploadInspectionImageview)
				}
				override fun onNothingSelected(p0: AdapterView<*>?) {
					Log.d("initSectionSpinner", "Wrong Access")
				}
			}
		binding.sectionSpinner.isEnabled = true
	}
	private fun getSectionList(){
		binding.uploadImageNameTv.text = "이미지를 선택하세요."
		binding.uploadImageUploaderTv.text = "-"
		binding.uploadImageDateTv.text = "-"
		scaleAdapter.clearList()
		sectionList.clear()
		sectionArrayAdapter.clear()
		droneImageAdapter.clearImageList()
		context?.let { Glide.with(it).load(R.color.white).diskCacheStrategy(
			DiskCacheStrategy.NONE).skipMemoryCache(false).into(binding.uploadInspectionImageview) }
		if (model.directionState.value?.Name == "인천계통"){
			Log.d("getSectionList", "getSectionList로드_팔당")
			scaleAdapter.addSectionList("팔당")
			sectionList.add(Section("팔당", "PL1"))
			sectionArrayAdapter.add("팔당")
			droneImageAdapter.clearImageList()
			if (activity != null && isAdded){
				getDroneImageList("팔당")
			}
		}else if (model.directionState.value?.Name == "평택계통"){
			Log.d("getSectionList", "getSectionList로드_ 성남")
			scaleAdapter.addSectionList("성남")
			sectionList.add(Section("성남", "PL1"))
			sectionArrayAdapter.add("성남")
			droneImageAdapter.clearImageList()
			if (activity != null && isAdded) {
				getDroneImageList("성남")
			}
		}
		/*CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getCstSectionList(model.facilityState.value!!.Code + File.separator + model.directionState.value!!.Code + File.separator + model.keyPlanState.value + File.separator + model.planeState.value + File.separator + File.separator + model.planeSectionState.value!!.code).enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (response.isSuccessful) {
						Log.d("getSectionList", "getSectionList로드2")
						for (i in response.body()!!.getAsJsonArray("sectionDirList").asJsonArray){
							scaleAdapter.addSectionList(i.asJsonObject.get("name").asString)
							sectionList.add(Section(i.asJsonObject.get("name").asString, i.asJsonObject.get("code").asString))
							sectionArrayAdapter.add(i.asJsonObject.get("name").asString)
						}
					} else {
						Log.d("getCstFacility", "response 실패${response.errorBody()}")
					}
				}
				
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
					Log.d("getCstFacility", "response 실패2${t}")
				}
			})
		}*/
	}
	private fun getDroneImageList(name: String){
		if (name == "팔당"){
			for(i in 169 .. 205){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang1_max_0" + i,
					resources.getIdentifier("paldang1_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 206 .. 241){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang2_max_0" + i,
					resources.getIdentifier("paldang2_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 242 .. 276){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang3_max_0" + i,
					resources.getIdentifier("paldang3_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 277 .. 319){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang4_max_0" + i,
					resources.getIdentifier("paldang4_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 320 .. 362){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang5_max_0" + i,
					resources.getIdentifier("paldang5_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 363 .. 403){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang6_max_0" + i,
					resources.getIdentifier("paldang6_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 404 .. 441){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang7_max_0" + i,
					resources.getIdentifier("paldang7_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 442 .. 477){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang8_max_0" + i,
					resources.getIdentifier("paldang8_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 478 .. 512){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang9_max_0" + i,
					resources.getIdentifier("paldang9_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 513 .. 551){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang10_max_0" + i,
					resources.getIdentifier("paldang10_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 559 .. 594){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang11_max_0" + i,
					resources.getIdentifier("paldang11_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 552 .. 627){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang12_max_0" + i,
					resources.getIdentifier("paldang12_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 628 .. 659){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang13_max_0" + i,
					resources.getIdentifier("paldang13_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 660 .. 692){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang14_max_0" + i,
					resources.getIdentifier("paldang14_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 693 .. 742){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang15_max_0" + i,
					resources.getIdentifier("paldang15_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 743 .. 785){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang16_max_0" + i,
					resources.getIdentifier("paldang16_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 786 .. 821){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang17_max_0" + i,
					resources.getIdentifier("paldang17_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 822 .. 874){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang18_max_0" + i,
					resources.getIdentifier("paldang18_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 694 .. 721){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang19_max_0" + i,
					resources.getIdentifier("paldang19_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 615 .. 656){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang20_max_0" + i,
					resources.getIdentifier("paldang20_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 580 .. 614){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang21_max_0" + i,
					resources.getIdentifier("paldang21_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 547 .. 579){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang22_max_0" + i,
					resources.getIdentifier("paldang22_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 511 .. 546){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang23_max_0" + i,
					resources.getIdentifier("paldang23_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 475 .. 510){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang24_max_0" + i,
					resources.getIdentifier("paldang24_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 433 .. 474){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang25_max_0" + i,
					resources.getIdentifier("paldang25_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 408 .. 432){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang26_max_0" + i,
					resources.getIdentifier("paldang26_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 370 .. 407){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang27_max_0" + i,
					resources.getIdentifier("paldang27_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 344 .. 369){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang28_max_0" + i,
					resources.getIdentifier("paldang28_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 318 .. 343){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang29_max_0" + i,
					resources.getIdentifier("paldang29_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 292 .. 317){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang30_max_0" + i,
					resources.getIdentifier("paldang30_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 269 .. 291){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang31_max_0" + i,
					resources.getIdentifier("paldang31_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 248 .. 268){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang32_max_0" + i,
					resources.getIdentifier("paldang32_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 223 .. 247){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang33_max_0" + i,
					resources.getIdentifier("paldang33_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 203 .. 222){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang34_max_0" + i,
					resources.getIdentifier("paldang34_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 182 .. 202){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang35_max_0" + i,
					resources.getIdentifier("paldang35_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 156 .. 181){
				droneImageAdapter.addImageList(DroneImageItem(
					"paldang36_max_0" + i,
					resources.getIdentifier("paldang36_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			binding.droneImageCountTv.text = "(Total: ${droneImageAdapter.itemCount} )"
		}else if (name == "성남"){
			for(i in 52 .. 54){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam1_max_00" + i,
					resources.getIdentifier("seongnam1_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 55 .. 57){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam2_max_00" + i,
					resources.getIdentifier("seongnam2_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 58 .. 61){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam3_max_00" + i,
					resources.getIdentifier("seongnam3_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 62 .. 67){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam4_max_00" + i,
					resources.getIdentifier("seongnam4_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 68 .. 71){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam5_max_00" + i,
					resources.getIdentifier("seongnam5_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 72 .. 75){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam6_max_00" + i,
					resources.getIdentifier("seongnam6_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 76 .. 78){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam7_max_00" + i,
					resources.getIdentifier("seongnam7_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 79 .. 81){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam8_max_00" + i,
					resources.getIdentifier("seongnam8_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 82 .. 84){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam9_max_00" + i,
					resources.getIdentifier("seongnam9_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 85 .. 87){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam10_max_00" + i,
					resources.getIdentifier("seongnam10_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 88 .. 91){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam11_max_00" + i,
					resources.getIdentifier("seongnam11_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 92 .. 95){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam12_max_00" + i,
					resources.getIdentifier("seongnam12_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 96 .. 99){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam13_max_00" + i,
					resources.getIdentifier("seongnam13_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			droneImageAdapter.addImageList(DroneImageItem(
				"seongnam14_max_0099",
				resources.getIdentifier("seongnam14_max_0099", "drawable", requireActivity().packageName),
				"komapper",
				"2022-11-18",
				"",
				false
			))
			for(i in 100 .. 102){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam14_max_0" + i,
					resources.getIdentifier("seongnam14_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 103 .. 105){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam15_max_0" + i,
					resources.getIdentifier("seongnam15_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 103 .. 108){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam16_max_0" + i,
					resources.getIdentifier("seongnam16_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 109 .. 112){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam17_max_0" + i,
					resources.getIdentifier("seongnam17_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 113 .. 116){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam18_max_0" + i,
					resources.getIdentifier("seongnam18_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 117 .. 120){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam19_max_0" + i,
					resources.getIdentifier("seongnam19_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 121 .. 123){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam20_max_0" + i,
					resources.getIdentifier("seongnam20_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 124 .. 127){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam21_max_0" + i,
					resources.getIdentifier("seongnam21_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 94 .. 98){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam22_max_00" + i,
					resources.getIdentifier("seongnam22_max_00" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			droneImageAdapter.addImageList(DroneImageItem(
				"seongnam23_max_0099",
				resources.getIdentifier("seongnam23_max_0099", "drawable", requireActivity().packageName),
				"komapper",
				"2022-11-18",
				"",
				false
			))
			for(i in 100 .. 101){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam23_max_0" + i,
					resources.getIdentifier("seongnam23_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 102 .. 104){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam24_max_0" + i,
					resources.getIdentifier("seongnam24_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 105 .. 108){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam25_max_0" + i,
					resources.getIdentifier("seongnam25_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 109 .. 116){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam26_max_0" + i,
					resources.getIdentifier("seongnam26_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 117 .. 120){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam27_max_0" + i,
					resources.getIdentifier("seongnam27_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 121 .. 124){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam28_max_0" + i,
					resources.getIdentifier("seongnam28_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 125 .. 129){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam29_max_0" + i,
					resources.getIdentifier("seongnam29_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 130 .. 132){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam30_max_0" + i,
					resources.getIdentifier("seongnam30_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			for(i in 133 .. 135){
				droneImageAdapter.addImageList(DroneImageItem(
					"seongnam31_max_0" + i,
					resources.getIdentifier("seongnam31_max_0" + i, "drawable", requireActivity().packageName),
					"komapper",
					"2022-11-18",
					"",
					false
				))
			}
			binding.droneImageCountTv.text = "(Total: ${droneImageAdapter.itemCount} )"
		}
		/*CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getCstDroneImageList(model.facilityState.value!!.Code + File.separator + model.directionState.value!!.Code + File.separator + model.keyPlanState.value + File.separator + model.planeState.value + File.separator + model.planeSectionState.value!!.code, model.sectionState.value!!).enqueue(object : retrofit2.Callback<JsonObject> {
				@SuppressLint("SetTextI18n")
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (response.isSuccessful) {
						for (i in response.body()!!.getAsJsonArray("droneImageList").asJsonArray){
							droneImageAdapter.addImageList(DroneImageItem(
							i.asJsonObject.get("fileName").asString,
							i.asJsonObject.get("imagePath").asString,
							i.asJsonObject.get("uploader").asString,
							i.asJsonObject.get("date").asString,
							*//*i.asJsonObject.get("note").asString*//*"-",
								false
							))
						}
						binding.droneImageCountTv.text = "(Total: ${droneImageAdapter.itemCount} )"
					} else {
						Log.d("getCstFacility", "response 실패${response.errorBody()}")
					}
				}
				
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
					Log.d("getCstFacility", "response 실패2${t}")
				}
			})
		}*/
	}
	
	private fun dipToPixels(dipValue: Float): Float {
		return TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			dipValue,
			resources.displayMetrics
		)
	}
	enum class SlideDirection{
		UP,
		DOWN,
		LEFT,
		RIGHT
	}
	
	enum class SlideType{
		SHOW,
		HIDE
	}
	
	private fun View.slideAnimation(direction: SlideDirection, type: SlideType, duration: Long = 700){
		val fromX: Float
		val toX: Float
		val fromY: Float
		val toY: Float
		val array = IntArray(2)
		getLocationInWindow(array)
		if((type == SlideType.HIDE && (direction == SlideDirection.RIGHT || direction == SlideDirection.DOWN)) || (type == SlideType.SHOW && (direction == SlideDirection.LEFT || direction == SlideDirection.UP))){
			val displayMetrics = DisplayMetrics()
			val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
			windowManager.defaultDisplay.getMetrics(displayMetrics)
			val deviceWidth = displayMetrics.widthPixels
			val deviceHeight = displayMetrics.heightPixels
			array[0] = deviceWidth
			array[1] = deviceHeight
		}
		when (direction) {
			SlideDirection.UP -> {
				fromX = 0f
				toX = 0f
				fromY = if(type == SlideType.HIDE) 0f else (array[1] + height).toFloat()
				toY = if(type == SlideType.HIDE) -1f * (array[1] + height)  else 0f
			}
			SlideDirection.DOWN -> {
				fromX = 0f
				toX = 0f
				fromY = if(type == SlideType.HIDE) 0f else -1f * (array[1] + height)
				toY = if(type == SlideType.HIDE) 1f * (array[1] + height)  else 0f
			}
			SlideDirection.LEFT -> {
				fromX = if(type == SlideType.HIDE) 0f else 1f * (array[0] + width)
				toX = if(type == SlideType.HIDE) -1f * (array[0] + width) else 0f
				fromY = 0f
				toY = 0f
			}
			SlideDirection.RIGHT -> {
				fromX = if(type == SlideType.HIDE) 0f else -1f * (array[0] + width)
				toX = if(type == SlideType.HIDE) 1f * (array[0] + width) else 0f
				fromY = 0f
				toY = 0f
			}
		}
		val animate = TranslateAnimation(
			fromX,
			toX,
			fromY,
			toY
		)
		animate.duration = duration
		animate.setAnimationListener(object: Animation.AnimationListener{
			override fun onAnimationRepeat(animation: Animation?) {
			}
			
			override fun onAnimationEnd(animation: Animation?) {
				visibility = if(type == SlideType.HIDE){
					View.GONE
				}else{
					View.VISIBLE
				}
			}
			
			override fun onAnimationStart(animation: Animation?) {
				visibility = if(type == SlideType.SHOW){
					View.VISIBLE
				}else{
					View.GONE
				}
			}
		})
		startAnimation(animate)
	}
}