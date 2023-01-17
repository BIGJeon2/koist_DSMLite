package com.bigjeon.johoblite.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.bigjeon.johoblite.data.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    //LeftPanel
    private var _facilityState = MutableLiveData<Facility>()
    private var _projectState = MutableLiveData<Project>()
    private var _directionState = MutableLiveData<Direction>()
    private var _keyPlanState = MutableLiveData<String>()
    private var _planeState = MutableLiveData<String>()
    private var _planeSectionState = MutableLiveData<Plane>()
    private var _sectionState = MutableLiveData<String>()
    
    val facilityState : LiveData<Facility> get() = _facilityState
    val projectState : LiveData<Project> get() = _projectState
    val directionState : LiveData<Direction> get() = _directionState
    val keyPlanState : LiveData<String> get() = _keyPlanState
    val planeState : LiveData<String> get() = _planeState
    val planeSectionState : LiveData<Plane> get() = _planeSectionState
    val sectionState : LiveData<String> get() = _sectionState
    
    
    //Main
    private var _titleState = MutableLiveData<String>()
    private var _subTitleState = MutableLiveData<String>()
    private var _fragmentState = MutableLiveData<String>()
    
    val titleState : LiveData<String> get() = _titleState
    val subTitleState : LiveData<String> get() = _subTitleState
    val fragmentState : LiveData<String> get() = _fragmentState
    
    init {
        //Navigation
        _facilityState.value = Facility(0, "NONE", "NONE")
        _projectState.value = Project(0, "NONE", "NONE")
        _directionState.value = Direction("NONE", "NONE")
        _keyPlanState.value = "NONE"
        _planeState.value = "NONE"
        _planeSectionState.value = Plane("NONE", "NONE")
        _sectionState.value = "NONE"
        //Main
        _titleState.value = "HOME"
        _subTitleState.value = "Facility_Home"
        _fragmentState.value = "Facility_Home"
    }
    
    fun setFacility(facility: Facility){
        _facilityState.value = facility
    }
    fun setProject(project: Project){
        _projectState.value = project
    }
    fun setDirection(direction: Direction){
        _directionState.value = direction
    }
    fun setKeyPlan(keyPlan: String){
        _keyPlanState.value = keyPlan
    }
    fun setPlane(plane: String){
        _planeState.value = plane
    }
    fun setPlaneSection(section: Plane){
        _planeSectionState.value = section
    }
    fun setSection(section: String){
        _sectionState.value = section
    }
    fun setTitle(title: String){
        _titleState.value = title
    }
    fun setSubTitle(subTitle: String){
        _subTitleState.value = subTitle
    }
    fun setFragment(fragment: String){
        _fragmentState.value = fragment
    }
    
    ////////////////////////////////////////////////////////Home_Facility///////////////////////////////////////////////////////////////////
    private val mapTypeFilterState: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    fun getMapTypeFilterState(): LiveData<String>{
        return mapTypeFilterState
    }
    
    fun changeMapTypeFilterState(state: String){
        mapTypeFilterState.value = state
    }
    private val mapAccessState: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    fun getMapAccessState(): LiveData<Boolean>{
        return mapAccessState
    }
    
    fun changeMapAccessState(state: Boolean){
        mapAccessState.value = state
    }
    ////////////////////////////////////////////////////////Upload_StitchingInspection///////////////////////////////////////////////////////////////////
    //CST_Image
    private var _stitchedImage = MutableLiveData<String>()
    
    val stitchedImage : LiveData<String> get() = _stitchedImage
    
    fun setStitchedImage(url: String){
        _stitchedImage.value = url
    }
    
    //Facility
    private val mapAccessibleState: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    
    fun changeMapAccessibleState(state: Boolean){
        mapAccessibleState.value = state
    }
    
    //layerData
    val cstLayerData : LiveData<LayerData> get() = _cstLayerData
    val layerSelectedPosition : LiveData<Int> get() = _layerSelectedPosition
    val addedCrack : LiveData<Int> get() = _addedCrack
    
    
    
    private val _cstLayerData = MutableLiveData<LayerData>()
    private var _layerSelectedPosition = MutableLiveData<Int>()
    private var _addedCrack = MutableLiveData<Int>()
    
    fun setLayerSelectedPosition(pos: Int){
        _layerSelectedPosition.value = pos
    }
    fun setCStLayerData(layer: LayerData){
        _cstLayerData.value = layer
    }
    /*fun addCstCrack(crack: Crack){
        _cstLayerData.value?.Cst_Crack_List?.add(crack)
        Log.d("cstLayerSize:Add", "${cstLayerData.value?.Cst_Crack_List?.size}")
    }*/
    
    fun setAddedCrackCount(){
        _addedCrack.value = 0
    }
    
    fun addCrackCount(){
        _addedCrack.value = _addedCrack.value?.plus(1)
    }
    
    val cstCadLayerData : LiveData<ArrayList<CadLayer>> get() = _cstCadLayerData
    val cadlayerSelectedPosition : LiveData<Int> get() = _cadLayerSelectedPosition
    val addedCadCrack : LiveData<Int> get() = _addedCadCrack
    
    private val _cstCadLayerData = MutableLiveData<ArrayList<CadLayer>>()
    private var _cadLayerSelectedPosition = MutableLiveData<Int>()
    private var _addedCadCrack = MutableLiveData<Int>()
    
    fun setCadLayerSelectedPosition(pos: Int){
        _cadLayerSelectedPosition.value = pos
    }
    fun setCadCStLayerData(layerList: ArrayList<CadLayer>){
        _cstCadLayerData.value = layerList
    }
    fun setAddedCadCrackCount(){
        _addedCadCrack.value = 0
    }
    
    fun addCadCrackCount(){
        _addedCadCrack.value = _addedCadCrack.value?.plus(1)
    }
}