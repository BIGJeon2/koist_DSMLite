package com.bigjeon.johoblite.analysis

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.Crack
import com.bigjeon.johoblite.data.LayerData
import com.bigjeon.johoblite.databinding.FragmentCrackDataInputBinding
import com.bigjeon.johoblite.dialog.ConfirmDialog
import com.bigjeon.johoblite.dialog.CrackNoteDataInPut
import com.bigjeon.johoblite.inter.ConfirmDialogInterface
import com.bigjeon.johoblite.inter.ConfirmInterface
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.rcvadapter.DrawingToolRcvAdapter
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.material.color.MaterialColors
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.File
import kotlin.math.roundToInt


class CrackDataInputFragment : Fragment(), ConfirmDialogInterface, ConfirmInterface {
	
	private lateinit var binding: FragmentCrackDataInputBinding
	private val model by activityViewModels<MainViewModel>()
	private lateinit var drawingToolAdapter: DrawingToolRcvAdapter
	private var editable = false
	
	//retrofit2
	private lateinit var serviceClient: OcrFactory
	
	@SuppressLint("SetTextI18n", "UseCompatLoadingForColorStateLists")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentCrackDataInputBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val activity = requireActivity()
		val context = requireContext()
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		
		val circularProgressDrawable = CircularProgressDrawable(activity)
		circularProgressDrawable.strokeWidth = 5f
		circularProgressDrawable.centerRadius = 30f
		circularProgressDrawable.start()
		Glide.with(activity).load(circularProgressDrawable).into(binding.loadingImageview)
		
		initDrawingToolRcv()
		
		binding.scaleImageView.maxScale = 6f
		
		model.planeSectionState.observe(viewLifecycleOwner){ source ->
			if (source.code != "NONE"){
				setCrackData(null, 0)
				binding.scaleImageView.removeTextView()
				binding.scaleImageView.clearLayer()
				binding.scaleImageView.recycle()
				getStitchedImage()
			}
		}
		
		model.layerSelectedPosition.observe(requireActivity()) { source ->
			binding.scaleImageView.setSelectedPosition(source)
			if (source >= 0 && model.cstLayerData.value != null){
				setCrackData(model.cstLayerData.value?.Cst_Crack_List!![source], source)
			}
		}
		
		model.addedCrack.observe(requireActivity()){source ->
			binding.layerTitleTv.text = "2022년도 손상( Total: ${model.cstLayerData.value?.Cst_Crack_List?.size} )"
			if (source > 0){
				binding.saveBtn.setBackgroundResource(R.drawable.default_background_main)
				binding.saveBtn.setTextColor(context.getColor(R.color.white))
				binding.saveBtn.setOnClickListener {
					Toast.makeText(context, "저장 완료.", Toast.LENGTH_SHORT).show()
					model.setAddedCrackCount()
				}
			}else{
				binding.saveBtn.setBackgroundResource(R.drawable.default_disable_btn_background)
				binding.saveBtn.setTextColor(MaterialColors.getColor(context, R.attr.default_color, Color.BLACK))
				binding.saveBtn.setOnClickListener {
					Toast.makeText(context, "추가된 정보가 없습니다.", Toast.LENGTH_SHORT).show()
				}
			}
		}
		
		binding.remunerationDataEditBtn.setOnClickListener {
			if (model.layerSelectedPosition.value!! >= 0){
				noteEditDialog(
					model.cstLayerData.value?.Cst_Crack_List!![model.layerSelectedPosition.value!!],
					model.layerSelectedPosition.value
				)
			}
		}
		
		binding.layerVisibilityToggleBtn.setOnToggledListener { _, _ -> binding.scaleImageView.setLayerVisibility() }
		binding.numberingControlSwitch.setOnToggledListener{ _, _ -> binding.scaleImageView.setNumberingState()}
		binding.editModeBtn.setOnClickListener{
			binding.scaleImageView.setMode("NONE")
			drawingToolAdapter.changePosition(0)
			if (editable){
				if (model.addedCrack.value!! > 0){
					addedCrackSaveDialog()
				}else{
					editable = false
					binding.editModeBtn.text = "CONVERT TO EDIT"
					binding.toolContainer.slideAnimation(SlideDirection.RIGHT, SlideType.HIDE)
					binding.remunerationContainer.slideAnimation(SlideDirection.LEFT, SlideType.SHOW)
				}
			}else{
				binding.scaleImageView.setMode("NONE")
				editable = true
				model.setAddedCrackCount()
				binding.editModeBtn.text = "CONVERT TO VIEW"
				binding.toolContainer.slideAnimation(SlideDirection.LEFT, SlideType.SHOW)
				binding.remunerationContainer.slideAnimation(SlideDirection.RIGHT, SlideType.HIDE)
			}
		}
	}
	
	private fun getStitchedImage(){
		binding.loadingImageview.visibility = View.VISIBLE
		CoroutineScope(Dispatchers.IO).launch {
			serviceClient.getCstStitchedImage(model.facilityState.value!!.Code + File.separator + model.directionState.value!!.Code + File.separator + model.keyPlanState.value + File.separator + model.planeState.value + File.separator + model.planeSectionState.value!!.code, "all" + "").enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					if (response.isSuccessful && context != null && response.body()?.get("result")!!.asString.equals("true")) {
						model.setStitchedImage(getString(R.string.URL) + "data/" + response.body()!!.getAsJsonObject("stitchedImageInfo").asJsonObject.getAsJsonPrimitive("path").asString)
						when (model.keyPlanState.value.toString()){
							"P21" -> {
								binding.scaleImageView.setScale(2319, 2200)
							}
							"P22" -> {
								binding.scaleImageView.setScale(2376, 2200)
							}
							"P23" -> {
								binding.scaleImageView.setScale(2433, 2200)
							}
							"P04" -> {
								binding.scaleImageView.setScale(1369, 440)
							}
							"P05" -> {
								binding.scaleImageView.setScale(935, 440)
							}
						}
						loadStitchedImage()
					} else {
						model.setStitchedImage("NONE")
					}
				}
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
					model.setStitchedImage("NONE")
				}
			})
		}
	}
	
	private fun getCrackLayer(){
		binding.scaleImageView.clearLayer()
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
							crackList.add(Crack(i.asJsonObject.get("ID").asInt, i.asJsonObject.get("TYPE").asString, i.asJsonObject.get("WIDTH").asFloat ,i.asJsonObject.get("LENGTH").asFloat ,pointList, false,"NONE"))
						}
						model.setCStLayerData(LayerData(model.projectState.value!!.projectCode, crackList))
						binding.scaleImageView.setLayer(LayerData(model.projectState.value!!.projectCode, crackList))
						binding.layerTitleTv.text = "2022년도 손상( Total: ${model.cstLayerData.value?.Cst_Crack_List?.size} )"
						Log.d("getSearchedFacility", "response${response.errorBody().toString()}")
					} else {
						binding.scaleImageView.setDrawable(false)
						Log.d("getSearchedFacility", "response 실패${response.errorBody().toString()}")
					}
				}
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
					Log.d("getSearchedFacility", "response 실패2${t}")
				}
			})
		}
	}
	
	private fun noteEditDialog(crack: Crack, id: Int?){
		val dialog = id?.let { CrackNoteDataInPut(getDisplaySize().x, getDisplaySize().y,this, crack, it) }
		if (dialog != null) {
			dialog.isCancelable = false
		}
		dialog?.show(activity?.supportFragmentManager!!, "Dialog")
	}
	
	private fun addedCrackSaveDialog(){
		val dialog = ConfirmDialog(getDisplaySize().x, getDisplaySize().y, this)
		dialog.isCancelable = false
		dialog.show(activity?.supportFragmentManager!!, "Dialog")
	}
	
	private fun getDisplaySize(): Point{
		val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
		val display = windowManager.defaultDisplay
		val size = Point()
		display.getSize(size)
		return size
	}
	
	override fun onSaveButtonClick(crack: Crack, id: Int?) {
		model.cstLayerData.value?.Cst_Crack_List?.set(id!!, crack)
		binding.remunerationDataTv.text = crack.note
		setCrackData(crack, id!!)
	}
	
	@SuppressLint("SetTextI18n")
	private fun setCrackData(crack: Crack?, pos: Int) = if (crack != null){
		binding.crackNumberTv.text = (pos + 1).toString()
		when(crack.crackType){
			"CRK001" -> {
				binding.crackTypeTv.text = "균열"
			}
			"CRK002" -> {
				binding.crackTypeTv.text = "망상 균열"
			}
			"CRK003" -> {
				binding.crackTypeTv.text = "박리, 들뜸"
			}
			"CRK004" -> {
				binding.crackTypeTv.text = "누수, 습운부"
			}
			"CRK005" -> {
				binding.crackTypeTv.text = "표면 부식"
			}
			"CRK006" -> {
				binding.crackTypeTv.text = "박락, 파손"
			}
			"CRK007" -> {
				binding.crackTypeTv.text = "철근 노출"
			}
			"CRK008" -> {
				binding.crackTypeTv.text = "재료 분리"
			}
			"CRK009" -> {
				binding.crackTypeTv.text = "백태"
			}
		}
		binding.crackAreaTv.text = ((crack.crackLength * crack.crackWidth * 100).roundToInt() / 100.0).toString() + "㎡"
		binding.crackInputTv.text = "A.I Detection"
		if (crack.repairState){
			binding.crackRepairStateTv.text = "보수 완료"
			context?.let { binding.crackRepairStateTv.setTextColor(it.getColor(R.color.default_color)) }
		}else{
			binding.crackRepairStateTv.text = "미완료"
			context?.let { binding.crackRepairStateTv.setTextColor(it.getColor(R.color.red)) }
		}
		if (crack.note == "NONE"){
			binding.remunerationDataTv.text = "-"
		}else{
			binding.remunerationDataTv.text = crack.note
		}
	}else{
		binding.crackNumberTv.text = "-"
		binding.crackTypeTv.text = "-"
		binding.crackAreaTv.text = "-"
		binding.crackInputTv.text = "-"
		binding.crackRepairStateTv.text = "-"
		binding.remunerationDataTv.text = "-"
		binding.crackRepairStateTv.setTextColor(requireContext().getColor(R.color.primary_text_color))
	}
	
	private fun initDrawingToolRcv(){
		drawingToolAdapter = DrawingToolRcvAdapter(requireContext())
		val lm = GridLayoutManager(context, 4).also { it.orientation = LinearLayoutManager.VERTICAL }
		binding.toolRcv.adapter = drawingToolAdapter
		binding.toolRcv.setHasFixedSize(false)
		binding.toolRcv.layoutManager = lm
		drawingToolAdapter.setOnItemClickListener(object : DrawingToolRcvAdapter.OnItemClickListener{
			override fun onItemClick(v: View, mode: String, position: Int) {
				binding.scaleImageView.setMode(mode)
				drawingToolAdapter.changePosition(position)
			}
		})
	}
	
	private fun loadStitchedImage(){
		model.setLayerSelectedPosition(-1)
		binding.scaleImageView.setDrawable(true)
		Glide.with(requireContext()).download(model.stitchedImage.value).into(object : CustomTarget<File>(){
			override fun onResourceReady(resource: File, transition: Transition<in File>?) {
				binding.scaleImageView.setImage(ImageSource.uri(Uri.fromFile(resource)))
				binding.scaleImageView.setOnImageEventListener(object : SubsamplingScaleImageView.OnImageEventListener{
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
			
			}
		})
		startAnimation(animate)
	}
	
	override fun onSaveButtonClick() {
		editable = false
		binding.editModeBtn.text = "CONVERT TO EDIT"
		binding.toolContainer.slideAnimation(SlideDirection.RIGHT, SlideType.HIDE)
		binding.remunerationContainer.slideAnimation(SlideDirection.LEFT, SlideType.SHOW)
		model.setAddedCrackCount()
	}
	
	override fun onCloseButtonClick() {
		model.setLayerSelectedPosition(-1)
		binding.scaleImageView.removeAddedCrack(model.addedCrack.value!!)
		editable = false
		binding.editModeBtn.text = "CONVERT TO EDIT"
		
		binding.toolContainer.slideAnimation(SlideDirection.RIGHT, SlideType.HIDE)
		binding.remunerationContainer.slideAnimation(SlideDirection.LEFT, SlideType.SHOW)
		model.setAddedCrackCount()
	}
	
	override fun onDetach() {
		super.onDetach()
		Glide.get(requireActivity()).clearMemory()
		binding.scaleImageView.recycle()
	}
	
}

