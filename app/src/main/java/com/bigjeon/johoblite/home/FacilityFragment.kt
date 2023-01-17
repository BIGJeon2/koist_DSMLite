package com.bigjeon.johoblite.home

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.AllFacility
import com.bigjeon.johoblite.databinding.FragmentFacilityBinding
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*


class FacilityFragment : Fragment(),OnMapReadyCallback{
	
	private lateinit var binding: FragmentFacilityBinding
	private val model: MainViewModel by activityViewModels()
	private var map : GoogleMap? = null
	private lateinit var searchedFacilityArrayAdapter : ArrayAdapter<String>
	private val searchedFacilityList = arrayListOf<String>()
	private var facilityTypeList = arrayListOf("Bridge", "Dam", "Port", "Else")
	private var allFacilityList = arrayListOf<AllFacility>()
	//retrofit2
	private lateinit var serviceClient: OcrFactory
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentFacilityBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		
		binding.googleMap.onCreate(savedInstanceState)
		binding.googleMap.getMapAsync(this)
		
		initFilteringView()
		initSearchedFacilityListView()
		
		binding.facilityMapFilterBtn.setOnClickListener {
			showFiltering()
		}
		
		model.directionState.observe(requireActivity()){source ->
			if (source.Name != "NONE"/* && binding.mapSearchView.query != source.Name*/){
				setFacilityInfo()
			}
		}
		
	}
	
	override fun onMapReady(googleMap: GoogleMap) {
		map = googleMap
		//서해대교 좌표로 이동
		if(model.directionState.value?.Name == "인천계통") {
			val myLocation = LatLng(37.5155, 127.2799)
			map?.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
		}else if(model.directionState.value?.Name == "평택계통"){
			val myLocation = LatLng(37.411944, 127.10944)
			map?.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
		}else{
			val myLocation = LatLng(37.5155, 127.2799)
			map?.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
		}
		val markerOption1 = MarkerOptions().position(LatLng(37.5155, 127.2799)).title("팔당 조압수조").snippet("경기도 하남시 배알미동 18 - 3")
		val markerOption2 = MarkerOptions().position(LatLng(37.411944, 127.10944)).title("성남 조압수조").snippet("경기도 성남시 수정구 사송동 512")
		val marker1 = map?.addMarker(markerOption1)!!
		val marker2 = map?.addMarker(markerOption2)!!
		map?.moveCamera(CameraUpdateFactory.zoomTo(15f))
		map?.mapType = GoogleMap.MAP_TYPE_HYBRID
		/*val east = map.projection.visibleRegion.latLngBounds.northeast.longitude
		val north = map.projection.visibleRegion.latLngBounds.northeast.latitude
		val west = map.projection.visibleRegion.latLngBounds.southwest.longitude
		val south = map.projection.visibleRegion.latLngBounds.southwest.latitude
		val jsonObject = Gson().toJson(Region(east,north,west,south))*/
		
		map?.setOnMarkerClickListener {
			setFacilityInfo2(it.title.toString())
			false
		}
		
		map?.setOnMapClickListener {
			binding.mapSearchView.clearFocus()
		}
	}
	
	/*private fun getFacilityInfo(name: String) {
		CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getSelectedFacilityInfo(name).enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (response.isSuccessful) {
						for (i in response.body()!!.get("facilInfo").asJsonArray){
							val fac = AllFacility(
								i.asJsonObject.get("ID").asString.toString(),
								i.asJsonObject.get("NAME").asString.toString(),
								i.asJsonObject.get("SAFECHK_RESULT").toString(),
								i.asJsonObject.get("SAFECHK_TYPE").asString.toString(),
								i.asJsonObject.get("SAFECHK_DATE").toString(),
								i.asJsonObject.get("INSTITUTION_NM").toString(),
								i.asJsonObject.get("REFERENCE_DATE").toString(),
								i.asJsonObject.get("ADDRESS").asString.toString(),
								i.asJsonObject.get("LENGTH").asString.toString(),
								i.asJsonObject.get("WIDTH").asString.toString(),
								i.asJsonObject.get("HEIGHT").asString.toString(),
								i.asJsonObject.get("SPRSTRCT_FOM").toString(),
								i.asJsonObject.get("COMPET_YEAR").toString(),
								i.asJsonObject.get("LNG").toString(),
								i.asJsonObject.get("LAT").toString()
							)
							setFacilityInfo(fac)
							if (map != null){
								map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(fac.LAT.toDouble(), fac.LNG.toDouble())))
								map?.moveCamera(CameraUpdateFactory.zoomTo(15f))
								val markerOption = MarkerOptions().position(LatLng(fac.LAT.toDouble(), fac.LNG.toDouble())).title(fac.NAME).snippet(fac.ADDRESS)
								val marker = map?.addMarker(markerOption)
								if (marker != null) {
									marker.tag = fac.NAME
								}
								marker!!.showInfoWindow()
							}
						}
						Log.d("getSearchedFacility", "response${response.errorBody().toString()}")
					} else {
						Log.d("getSearchedFacility", "response 실패${response.errorBody().toString()}")
					}
				}
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
					Log.d("getSearchedFacility", "response 실패2${t}")
				}
			})
		}
	}*/
	
	/*private fun getSearchedFacilityList(name: String){
		searchedFacilityArrayAdapter.clear()
		CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getSearchedCstFacilityList(name).enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (response.isSuccessful) {
						for (i in response.body()!!.getAsJsonArray("facilityList").asJsonArray){
							searchedFacilityList.add(i.asJsonObject.get("NAME").asString)
							searchedFacilityArrayAdapter.notifyDataSetChanged()
						}
						Log.d("getSearchedFacility", "response${response.errorBody().toString()}")
					} else {
						Log.d("getSearchedFacility", "response 실패${response.errorBody().toString()}")
					}
				}
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
					Log.d("getSearchedFacility", "response 실패2${t}")
				}
			})
		}
	}*/
	
	private fun setFacilityInfo2(pos: String){
		if(pos == "팔당 조압수조"){
			binding.facilityNameTv.text = "인천계통 조압수조(팔당)"
			binding.facilitySAFECHKRESULTTv.text = "-"
			binding.facilitySAFECHKDATETv.text = "22.12.21"
			binding.facilitySAFECHKTYPETv.text = "디지털 외관조사망도 구축"
			binding.facilityINSTITUTIONNMTv.text = "국토안전관리원"
			binding.facilityREFERENCEDATETv.text = "22.12.21"
			binding.facilityADDRESSTv.text = "경기도 하남시 배알미동 18 - 3"
			binding.facilityLENGTHTv.text = "-"
			binding.facilityWIDTHTv.text = "최대 : 14.78m / 최소 : 11.93m"
			binding.facilityHEIGHTTv.text = "32.6mm"
			binding.facilitySPRSTRCTFOMTv.text = "-"
			binding.facilityCOMPETYEARTv.text = "-"
			map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(37.5155, 127.2799)))
		}else if(pos == "성남 조압수조"){
			binding.facilityNameTv.text = "평택계통 조압수조(성남)"
			binding.facilitySAFECHKRESULTTv.text = "-"
			binding.facilitySAFECHKDATETv.text = "22.12.21"
			binding.facilitySAFECHKTYPETv.text = "디지털 외관조사망도 구축"
			binding.facilityINSTITUTIONNMTv.text = "국토안전관리원"
			binding.facilityREFERENCEDATETv.text = "22.12.21"
			binding.facilityADDRESSTv.text = "경기도 성남시 수정구 사송동 512"
			binding.facilityLENGTHTv.text = "-"
			binding.facilityWIDTHTv.text = "15.865m"
			binding.facilityHEIGHTTv.text = "최대 : 5.25m / 최소 : 5.00m"
			binding.facilitySPRSTRCTFOMTv.text = "-"
			binding.facilityCOMPETYEARTv.text = "-"
			map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(37.411944, 127.10944)))
		}else{
			binding.facilityNameTv.text = "-"
			binding.facilitySAFECHKRESULTTv.text = "-"
			binding.facilitySAFECHKDATETv.text = "-"
			binding.facilitySAFECHKTYPETv.text = "-"
			binding.facilityINSTITUTIONNMTv.text = "-"
			binding.facilityREFERENCEDATETv.text = "-"
			binding.facilityADDRESSTv.text = "-"
			binding.facilityLENGTHTv.text = "-"
			binding.facilityWIDTHTv.text = "-"
			binding.facilityHEIGHTTv.text = "-"
			binding.facilitySPRSTRCTFOMTv.text = "-"
			binding.facilityCOMPETYEARTv.text = "-"
		}
		
	}
	
	private fun setFacilityInfo(){
		if(model.directionState.value?.Name == "인천계통"){
			binding.facilityNameTv.text = "인천계통 조압수조(팔당)"
			binding.facilitySAFECHKRESULTTv.text = "-"
			binding.facilitySAFECHKDATETv.text = "22.12.21"
			binding.facilitySAFECHKTYPETv.text = "디지털 외관조사망도 구축"
			binding.facilityINSTITUTIONNMTv.text = "국토안전관리원"
			binding.facilityREFERENCEDATETv.text = "22.12.21"
			binding.facilityADDRESSTv.text = "경기도 하남시 배알미동 18 - 3"
			binding.facilityLENGTHTv.text = "-"
			binding.facilityWIDTHTv.text = "최대 : 14.78m / 최소 : 11.93m"
			binding.facilityHEIGHTTv.text = "32.6mm"
			binding.facilitySPRSTRCTFOMTv.text = "-"
			binding.facilityCOMPETYEARTv.text = "-"
			map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(37.5155, 127.2799)))
		}else if(model.directionState.value?.Name == "평택계통"){
			binding.facilityNameTv.text = "평택계통 조압수조(성남)"
			binding.facilitySAFECHKRESULTTv.text = "-"
			binding.facilitySAFECHKDATETv.text = "22.12.21"
			binding.facilitySAFECHKTYPETv.text = "디지털 외관조사망도 구축"
			binding.facilityINSTITUTIONNMTv.text = "국토안전관리원"
			binding.facilityREFERENCEDATETv.text = "22.12.21"
			binding.facilityADDRESSTv.text = "경기도 성남시 수정구 사송동 512"
			binding.facilityLENGTHTv.text = "-"
			binding.facilityWIDTHTv.text = "15.865m"
			binding.facilityHEIGHTTv.text = "최대 : 5.25m / 최소 : 5.00m"
			binding.facilitySPRSTRCTFOMTv.text = "-"
			binding.facilityCOMPETYEARTv.text = "-"
			map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(37.411944, 127.10944)))
		}else{
			binding.facilityNameTv.text = "-"
			binding.facilitySAFECHKRESULTTv.text = "-"
			binding.facilitySAFECHKDATETv.text = "-"
			binding.facilitySAFECHKTYPETv.text = "-"
			binding.facilityINSTITUTIONNMTv.text = "-"
			binding.facilityREFERENCEDATETv.text = "-"
			binding.facilityADDRESSTv.text = "-"
			binding.facilityLENGTHTv.text = "-"
			binding.facilityWIDTHTv.text = "-"
			binding.facilityHEIGHTTv.text = "-"
			binding.facilitySPRSTRCTFOMTv.text = "-"
			binding.facilityCOMPETYEARTv.text = "-"
		}
	
		/*if (fac.NAME != model.facilityState.value?.Name){
			(activity as MainActivity).checkAccessibleFacility(fac.NAME)
		}
		binding.facilityNameTv.text = fac.NAME
		if (fac.SAFECHK_RESULT == "null"){
			binding.facilitySAFECHKRESULTTv.text = "-"
		}else{
			binding.facilitySAFECHKRESULTTv.text = fac.SAFECHK_RESULT
		}
		binding.facilitySAFECHKTYPETv.text = fac.SAFECHK_TYPE
		if (fac.SAFECHK_DATE == "null"){
			binding.facilitySAFECHKDATETv.text = "-"
		}else{
			binding.facilitySAFECHKDATETv.text = fac.SAFECHK_DATE
		}
		if (fac.INSTITUTION_NM == "null"){
			binding.facilityINSTITUTIONNMTv.text = "-"
		}else{
			binding.facilityINSTITUTIONNMTv.text = fac.INSTITUTION_NM
		}
		if (fac.REFERENCE_DATE == "null"){
			binding.facilityREFERENCEDATETv.text = "-"
		}else{
			binding.facilityREFERENCEDATETv.text = fac.REFERENCE_DATE
		}
		binding.facilityADDRESSTv.text = fac.ADDRESS
		binding.facilityLENGTHTv.text = fac.LENGTH
		binding.facilityWIDTHTv.text = fac.WIDTH
		binding.facilityHEIGHTTv.text = fac.HEIGHT
		if (fac.SPRSTRCT_FOM == "null"){
			binding.facilitySPRSTRCTFOMTv.text = "-"
		}else{
			binding.facilitySPRSTRCTFOMTv.text = fac.SPRSTRCT_FOM
		}
		if (fac.COMPET_YEAR == "null"){
			binding.facilityCOMPETYEARTv.text = "-"
		}else{
			binding.facilityCOMPETYEARTv.text = fac.COMPET_YEAR
		}*/
	}
	
	private fun initSearchedFacilityListView(){
		searchedFacilityArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, searchedFacilityList)
		binding.searchedFacilityListView.adapter = searchedFacilityArrayAdapter
		binding.searchedFacilityListView.onItemClickListener =
			AdapterView.OnItemClickListener { _, _, position, _ -> binding.mapSearchView.setQuery(searchedFacilityList[position], true) }
	}
	
	private fun initFilteringView() = with(binding){
		mapFilteringSpinner.adapter =
			context?.let { ArrayAdapter(it, R.layout.spinnertv, facilityTypeList).also { arrayAdapter ->  arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)} }
		mapFilteringSpinner.dropDownVerticalOffset =
			dipToPixels(40f).toInt()
		mapFilteringSpinner.onItemSelectedListener =
			object : AdapterView.OnItemSelectedListener {
				override fun onItemSelected(
					adapterView: AdapterView<*>?,
					view: View?,
					position: Int,
					id: Long
				) {
					model.changeMapTypeFilterState(facilityTypeList[position])
				}
				
				override fun onNothingSelected(p0: AdapterView<*>?) {
				
				}
			}
		mapCheckBox.setOnClickListener {
			if (mapCheckBox.isChecked){
				model.changeMapAccessibleState(true)
			}else{
				model.changeMapAccessibleState(true)
			}
		}
		mapCloseBtn.setOnClickListener {
			mapFilteringContainer.visibility = View.GONE
		}
	}
	
	/*private fun getFacility(json: String){
		CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getFacilityList(json).enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (response.isSuccessful) {
						for (i in response.body()!!.getAsJsonArray("facilityList").asJsonArray){
							Log.d("FACILITYCHECK", i.asJsonObject.get("NAME").asString.toString())
							val facilityItem = AllFacility(
								i.asJsonObject.get("ID").asString.toString(),
								i.asJsonObject.get("NAME").asString.toString(),
								i.asJsonObject.get("SAFECHK_RESULT").toString(),
								i.asJsonObject.get("SAFECHK_TYPE").asString.toString(),
								i.asJsonObject.get("SAFECHK_DATE").toString(),
								i.asJsonObject.get("INSTITUTION_NM").toString(),
								i.asJsonObject.get("REFERENCE_DATE").toString(),
								i.asJsonObject.get("ADDRESS").asString.toString(),
								i.asJsonObject.get("LENGTH").asString.toString(),
								i.asJsonObject.get("WIDTH").asString.toString(),
								i.asJsonObject.get("HEIGHT").asString.toString(),
								i.asJsonObject.get("SPRSTRCT_FOM").toString(),
								i.asJsonObject.get("COMPET_YEAR").toString(),
								i.asJsonObject.get("LNG").toString(),
								i.asJsonObject.get("LAT").toString()
							)
							allFacilityList.add(facilityItem)
							val markerOption = MarkerOptions().position(LatLng(facilityItem.LAT.toDouble(), facilityItem.LNG.toDouble())).title(facilityItem.NAME).snippet(facilityItem.ADDRESS)
							val marker = map?.addMarker(markerOption)
							if (marker != null) {
								marker.tag = facilityItem.NAME
							}
						}
					}
				}
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
				}
			})
		}
	}*/
	
	private fun showFiltering() = with(binding){
		mapFilteringContainer.visibility = View.VISIBLE
	}
	
	private fun dipToPixels(dip: Float): Float {
		return TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			dip,
			resources.displayMetrics
		)
	}
	
	override fun onStart() {
		binding.googleMap.onStart()
		super.onStart()
	}
	
	override fun onStop() {
		binding.googleMap.onStop()
		super.onStop()
	}
	
	override fun onResume() {
		binding.googleMap.onResume()
		super.onResume()
	}
	
	override fun onPause() {
		binding.googleMap.onPause()
		super.onPause()
	}
	
	override fun onLowMemory() {
		binding.googleMap.onLowMemory()
		super.onLowMemory()
	}
	
	override fun onDestroy() {
		binding.googleMap.onDestroy()
		super.onDestroy()
	}
	
}
