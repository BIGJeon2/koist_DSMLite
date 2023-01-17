package com.bigjeon.johoblite.analysis

import android.annotation.SuppressLint
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.Crack
import com.bigjeon.johoblite.data.DetectionCrack
import com.bigjeon.johoblite.data.LayerData
import com.bigjeon.johoblite.databinding.FragmentDetectionInspectBinding
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.rcvadapter.CrackLayerAdapter
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.File


internal class DetectionInspectFragment : Fragment() {
	
	private lateinit var binding: FragmentDetectionInspectBinding
	private val model by activityViewModels<MainViewModel>()
	//retrofit2
	private lateinit var serviceClient: OcrFactory
	
	lateinit var layerAdapter: CrackLayerAdapter
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
	): View {
		binding = FragmentDetectionInspectBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val activity = requireActivity()
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		
		binding.layerVisibilityToggleBtn.setOnToggledListener { _, _ -> binding.stitchedImageView.setLayerVisibility() }
		
		val circularProgressDrawable = CircularProgressDrawable(activity)
		circularProgressDrawable.strokeWidth = 5f
		circularProgressDrawable.centerRadius = 30f
		circularProgressDrawable.start()
		Glide.with(activity).load(circularProgressDrawable).into(binding.loadingImageview)
		
		initLayerRcv()
		
		binding.stitchedImageView.maxScale = 6f
		
		model.planeSectionState.observe(viewLifecycleOwner){ source ->
			if (source.code != "NONE"){
				binding.stitchedImageView.removeTextView()
				binding.stitchedImageView.clearLayer()
				binding.stitchedImageView.recycle()
				getStitchedImage()
			}
		}
		
		model.layerSelectedPosition.observe(requireActivity()) { source ->
			if (source >= 0 && model.cstLayerData.value != null){
				layerAdapter.setSelectedPosition(source)
				binding.stitchedImageView.setSelectedPosition(source)
				binding.detectionLayerRcv.smoothScrollToPosition(source)
			}
		}
	}
	
	private fun getStitchedImage(){
		binding.loadingImageview.visibility = View.VISIBLE
		CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getCstStitchedImage(model.facilityState.value!!.Code + File.separator + model.directionState.value!!.Code + File.separator + model.keyPlanState.value + File.separator + model.planeState.value + File.separator + model.planeSectionState.value!!.code, "all" +
					"").enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (context != null && response.isSuccessful && response.body()?.get("result")!!.asString.equals("true")) {
						model.setStitchedImage(getString(R.string.URL) + "data/" + response.body()!!.getAsJsonObject("stitchedImageInfo").asJsonObject.getAsJsonPrimitive("path").asString)
						getLayer()
					} else {
						model.setStitchedImage("NONE")
						binding.loadingImageview.visibility = View.GONE
						Log.d("getStitchedImage", "response 실패${response.errorBody()}")
					}
				}
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
					model.setStitchedImage("NONE")
					binding.loadingImageview.visibility = View.GONE
					Log.d("getStitchedImage", "response 실패2${t}")
				}
			})
		}
	}
	
	private fun getCrackLayer(){
		layerAdapter.clearLayerList()
		model.setLayerSelectedPosition(-1)
		CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getCstCrackLayer(model.facilityState.value!!.Code + File.separator + model.directionState.value!!.Code + File.separator + model.keyPlanState.value + File.separator + model.planeState.value + File.separator + model.planeSectionState.value!!.code).enqueue(object : retrofit2.Callback<JsonObject> {
				@SuppressLint("SetTextI18n")
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (response.isSuccessful) {
						val crackList = ArrayList<Crack>()
						for (i in response.body()!!.get("damageInfoList").asJsonArray){
							val pointList = ArrayList<PointF>()
							for (j in i.asJsonObject.getAsJsonArray("DAMAGE_PATH").asJsonArray){
								var x = 0f
								var y = 0f
								for (l in j.asJsonArray){
									if(l.asString != "M" && l.asString != "Q" && l.asString != "L"){
										//그 이외의 교각은 / 5
										if( x == 0f){
											x = l.asFloat / 10
										}else if (x != 0f && y == 0f){
											y = l.asFloat / 10
											pointList.add(PointF(x , y))
											x = 0f
											y = 0f
										}
									}
								}
							}
							layerAdapter.addCrack(DetectionCrack(i.asJsonObject.get("ID").asInt, i.asJsonObject.get("TYPE").asString, i.asJsonObject.get("WIDTH").asFloat ,i.asJsonObject.get("LENGTH").asFloat, false, "NONE"))
							crackList.add(Crack(i.asJsonObject.get("ID").asInt, i.asJsonObject.get("TYPE").asString, i.asJsonObject.get("WIDTH").asFloat ,i.asJsonObject.get("LENGTH").asFloat ,pointList,false, "NONE"))
						}
						binding.layerSizeTv.text = "2022년도 손상( Total: ${crackList.size} )"
						binding.stitchedImageView.setLayer(LayerData(model.projectState.value!!.projectCode, crackList))
						model.setCStLayerData(LayerData(model.projectState.value!!.projectCode, crackList))
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
	}
	
	private fun initLayerRcv(){
		val lm = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
		layerAdapter = CrackLayerAdapter(requireContext())
		binding.detectionLayerRcv.adapter = layerAdapter
		binding.detectionLayerRcv.setHasFixedSize(false)
		binding.detectionLayerRcv.layoutManager = lm
		layerAdapter.setOnItemClickListener(object: CrackLayerAdapter.OnItemClickListener{
			override fun onItemClick(v: View, position: Int) {
				model.setLayerSelectedPosition(position)
			}
		})
	}
	
	private fun getLayer(){
		model.setLayerSelectedPosition(-1)
		binding.stitchedImageView.setDrawable(false)
		Glide.with(requireContext()).download(model.stitchedImage.value).into(object : CustomTarget<File>(){
			override fun onResourceReady(resource: File, transition: Transition<in File>?) {
				binding.stitchedImageView.setImage(ImageSource.uri(Uri.fromFile(resource)))
				binding.stitchedImageView.setOnImageEventListener(object : SubsamplingScaleImageView.OnImageEventListener{
					override fun onReady() {
						binding.loadingImageview.visibility = View.GONE
					}
					override fun onImageLoaded() {
						resource.delete()
						Glide.get(requireContext()).clearMemory()
					}
					override fun onPreviewLoadError(e: Exception?) {
					}
					override fun onImageLoadError(e: Exception?) {
					}
					override fun onTileLoadError(e: Exception?) {
					}
					override fun onPreviewReleased() {
					}
				})
			}
			
			override fun onLoadCleared(placeholder: Drawable?) {
				Glide.get(requireContext()).clearMemory()
			}
		})
		getCrackLayer()
	}
	
	override fun onDetach() {
		super.onDetach()
		Glide.get(requireActivity()).clearMemory()
		//SubSamplingScaleImageView의 이미지가 Native Heap 영역에 할당 되기때문에 GC(Garbage Collection)의 대상이 되지 않는다. 따라서 Recycle()을 실행해줘야 불필요한 메모리가 사라진다.
		binding.stitchedImageView.recycle()
	}
}