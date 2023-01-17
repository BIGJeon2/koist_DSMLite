package com.bigjeon.johoblite.home

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.anim.ToggleAnimation
import com.bigjeon.johoblite.data.*
import com.bigjeon.johoblite.databinding.FragmentProgressBinding
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.rcvadapter.ProgressRcvAdapter
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.File


class ProgressFragment : Fragment() {
	
	private lateinit var binding: FragmentProgressBinding
	private val directionSpinnerList = arrayListOf("구역 선택")
	private val directionList = arrayListOf(Direction("NONE", "NONE"))
	private lateinit var progressAdapter : ProgressRcvAdapter
	private lateinit var directionArrayAdapter : ArrayAdapter<String>
	private val model: MainViewModel by activityViewModels()
	
	//retrofit2
	private lateinit var serviceClient: OcrFactory
	private var progressList = arrayListOf<ProgressItem>()
	
	private var progressExampleExpandState = false
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentProgressBinding.inflate(inflater, container, false)
		
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		
		val context = requireContext()
		
		binding.progressStatusExampleOpenBtn.setOnClickListener {
			val show = toggleLayout(!progressExampleExpandState, null, binding.progressStatusExampleView)
			progressExampleExpandState = show
		}
		binding.progressExampleCloseBtn.setOnClickListener {
			val show = toggleLayout(!progressExampleExpandState, null, binding.progressStatusExampleView)
			progressExampleExpandState = show
		}
		
		directionSpinnerAdapter()
		
		progressAdapter = ProgressRcvAdapter(context, progressList)
		val lm = LinearLayoutManager(context).also { it.orientation = LinearLayoutManager.VERTICAL }
		binding.progressPlaneRcv.adapter = progressAdapter
		binding.progressPlaneRcv.layoutManager = lm
		binding.progressPlaneRcv.setHasFixedSize(false)
		
		model.facilityState.observe(requireActivity()){ source ->
			if (source.Code != "NONE"){
				binding.totalProgressView.progress = 100f
				binding.totalProgressTv.text = "Completed"
				binding.totalProgressTv.setTextColor(context.resources.getColor(R.color.white, null))
				directionList.clear()
				directionArrayAdapter.clear()
				getProjectDirectionList()
			}else{
				directionList.clear()
				directionArrayAdapter.clear()
				progressAdapter.clearList()
				directionList.add(Direction("NONE", "NONE"))
				directionArrayAdapter.add("구역 선택")
				binding.totalProgressView.progress = 0f
				binding.totalProgressTv.text = "시설물을 선택하세요."
				binding.totalProgressTv.setTextColor(context.resources.getColor(R.color.default_color, null))
			}
		}
		
		return binding.root
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
	
	private fun getProjectDirectionList() {
		CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getCstProjectList(model.facilityState.value!!.Code).enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (response.isSuccessful) {
						for (i in response.body()?.get("directionInfo")!!.asJsonArray){
							directionList.add(Direction(i.asJsonObject.get("name").asString, i.asJsonObject.get("code").asString))
							directionArrayAdapter.add(i.asJsonObject.get("name").asString)
						}
					}
				}
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
				}
			})
		}
	}
	
	private fun directionSpinnerAdapter() {
		directionArrayAdapter = ArrayAdapter(requireContext(), R.layout.spinnertv, directionSpinnerList)
		directionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		binding.directionSpinner.adapter = directionArrayAdapter
		binding.directionSpinner.dropDownVerticalOffset =
			dipToPixels(40f).toInt()
		binding.directionSpinner.onItemSelectedListener =
			object : AdapterView.OnItemSelectedListener {
				override fun onItemSelected(
					adapterView: AdapterView<*>?,
					view: View?,
					position: Int,
					id: Long
				) {
					if (directionList[position].Code != "NONE"){
						progressAdapter.clearList()
						getKeyPlanList(directionList[position].Code)
					}else{
						progressAdapter.clearList()
					}
				}
				override fun onNothingSelected(p0: AdapterView<*>?) {
				
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
	
	private fun getKeyPlanList(code: String) {
		CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getCstKeyPlanList(model.facilityState.value!!.Code + File.separator + code).enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (response.isSuccessful) {
						for (i in response.body()?.getAsJsonArray("range")!!){
							progressAdapter.addItem(ProgressItem(i.asString, 100, arrayListOf(
								PlaneProgressItem("F", 5),
								PlaneProgressItem("B", 5),
								PlaneProgressItem("L", 7),
								PlaneProgressItem("R", 7),
								PlaneProgressItem("S", 7),
								PlaneProgressItem("U", 7)
							),
								false, impossible = false))
						}
					}
				}
				
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
				}
			})
		}
	}
	
}