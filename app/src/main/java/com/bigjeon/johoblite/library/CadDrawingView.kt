package com.bigjeon.johoblite.library

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.bigjeon.johoblite.MainActivity
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.CadCrackItem
import com.bigjeon.johoblite.data.CadLayer
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


class CadDrawingView : SubsamplingScaleImageView {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
	
	//S펜 적용
	private var isDraw = true
	
	//손상 리스트
	private var layerList = arrayListOf<CadLayer>()
	
	//스케일 값
	private var realHeight = 0
	private var realWidth = 0
	//DrawingSetting
	private var mode = "NONE"
	private var layerVisible = true
	private var numberingState = true
	private var moveState = false
	private var selectedItem = -1
	
	//Subsampling PreHandView
	private var vStart : PointF? = null
	private var vPrevious : PointF? = null
	private var vPrev = PointF()
	private val vPoint = PointF()
	private var path = Path()
	private var linePaint = Paint()
	private var patternPaint = Paint()
	private var textPaint = Paint()
	private var pointList = ArrayList<PointF>()
	
	//선 굵기
	private var strokeWidth = 0f
	
	init {
		val density = resources.displayMetrics.densityDpi.toFloat()
		strokeWidth = (density / 120f).toInt().toFloat()
		/*sPenStateCheck()*/
	}
	
	init {
		linePaint.strokeWidth = strokeWidth
		linePaint.style = Paint.Style.STROKE
		linePaint.textAlign = Paint.Align.CENTER
		
		patternPaint.strokeWidth = strokeWidth
		patternPaint.isAntiAlias = true
		patternPaint.isFilterBitmap = true
		patternPaint.isDither = true
		patternPaint.style = Paint.Style.FILL
		patternPaint.textAlign = Paint.Align.CENTER
		
		textPaint.strokeWidth = strokeWidth
		textPaint.style = Paint.Style.STROKE
		textPaint.textAlign = Paint.Align.CENTER
	}
	
	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean {
		val pointerCount = 0
		var consumed = false
		if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS && isReady ){
			when (event.action) {
				MotionEvent.ACTION_DOWN -> {
					if (event.actionIndex == 0) {
						Log.d("POINTFFFF", "${event.x} ;;; ${event.y}")
						vStart = PointF(event.x, event.y)
						vPrevious = PointF(event.x, event.y)
					} else {
						vStart = null
						vPrevious = null
					}
				}
				MotionEvent.ACTION_MOVE -> {
					moveState = true
					val sCurrentF = viewToSourceCoord(event.x, event.y)
					val sCurrent = PointF(sCurrentF!!.x, sCurrentF.y)
					val sStart = if (vStart == null && pointerCount == 1) null else PointF(viewToSourceCoord(vStart)!!.x, viewToSourceCoord(vStart)!!.y)
					if (sStart != null) {
						Log.d("POINTFFFF", "${sStart.x} ;;; ${sStart.y}")
					}
					if (mode != "NONE" && vStart != null) {
						val vDX = abs(event.x - vPrevious!!.x)
						val vDY = abs(event.y - vPrevious!!.y)
						if (vDX >= strokeWidth * 5 || vDY >= strokeWidth * 5) {
							if (pointList.isEmpty()){
								pointList = ArrayList()
								if (sStart != null) {
									pointList.add(sStart)
								}
							}
							pointList.add(sCurrent)
							vPrevious!!.x = event.x
							vPrevious!!.y = event.y
						}
						invalidate()
						consumed = true
					}else if (pointerCount == 1) {
						consumed = true
					}
				}
				MotionEvent.ACTION_UP -> {
					if (mode != "NONE" && pointList.size >= 3 && moveState && isDraw){
						val startPointF = PointF()
						val lastPointF = PointF()
						sourceToViewCoord(pointList[0], startPointF)!!
						sourceToViewCoord(pointList[pointList.size - 1], lastPointF)!!
						//실제 크기 측정(오차범위 +- 10)
						val realScaleHei : Double = (sHeight.toDouble() / realHeight)
						val realScaleWth : Double = (sWidth.toDouble() / realWidth)
						when(mode){
							"CRK001" -> {
								val radius = sqrt(
									((pointList[0].x - pointList[pointList.size - 1].x).toDouble()/realScaleWth).pow(2.0) +
											((pointList[0].y - pointList[pointList.size - 1].y).toDouble()/realScaleHei).pow(2.0)).toFloat()
								val crack = (CadCrackItem(0,mode,0.2f, radius/100, pointList, false, "NONE", false, false , 0))
								addCrackToParent(crack)
								Toast.makeText(context, "길이 : ${radius/100} (오차범위 +- ${0.01})", Toast.LENGTH_SHORT).show()
							}
							else -> {
								val width = sqrt(((pointList[0].x - pointList[pointList.size - 1].x).toDouble()/realScaleWth).pow(2.0)).toFloat()
								val hei = sqrt(((pointList[0].y - pointList[pointList.size - 1].y).toDouble()/realScaleHei).pow(2.0)).toFloat()
								val crack = (CadCrackItem(0,mode,width/100, hei/100, pointList, false, "NONE", false, false, 0))
								addCrackToParent(crack)
								Toast.makeText(context, "폭 : ${width/100}, 길이 : ${hei/100} (오차범위 +- ${0.01})", Toast.LENGTH_SHORT).show()
							}
						}
						invalidate()
					}
					vPrevious = null
					vStart = null
					moveState = false
					pointList = arrayListOf()
				}
			}
		}else{
			return super.onTouchEvent(event)
		}
		return consumed || super.onTouchEvent(event)
	}
	
	@SuppressLint("DrawAllocation", "UseCompatLoadingForDrawables")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas.apply {
			for (layer in layerList){
				if (layerVisible && canvas != null) {
					for (position in layer.Cst_Crack_List.indices){
						//ItemSelected
						if (0 <= selectedItem && selectedItem <= layer.Cst_Crack_List.size - 1 && layer.Cst_Crack_List[position] == layer.Cst_Crack_List[selectedItem]) {
							linePaint.color = context.getColor(R.color.green)
							linePaint.strokeWidth = strokeWidth * 2
						} else {
							linePaint.color = context.getColor(R.color.red)
							linePaint.strokeWidth = strokeWidth
						}
						//Draw
						when(layer.Cst_Crack_List[position].crackType){
							"TEXT" -> {
								path.reset()
								textPaint.textSize = (scale * layer.Cst_Crack_List[position].crackLength) * 1.3f
								textPaint.color = layer.layerColor
								sourceToViewCoord(layer.Cst_Crack_List[position].Path[0].x, layer.Cst_Crack_List[position].Path[0].y/* + ((layer.Cst_Crack_List[position].Path[0].y - layer.Cst_Crack_List[position].Path[1].y) / 3 * 2)*/, vPrev)
								path.moveTo(vPrev.x, vPrev.y)
								if (layer.Cst_Crack_List[position].allign == 1){
									textPaint.textAlign = Paint.Align.CENTER
								}else{
									textPaint.textAlign = Paint.Align.LEFT
								}
								/*paint.style = Paint.Style.FILL*/
								canvas.drawText(layer.Cst_Crack_List[position].note, vPrev.x, vPrev.y, textPaint)
							}
							"CIRCLE" -> {
								path.reset()
								linePaint.color = layer.layerColor
								sourceToViewCoord(layer.Cst_Crack_List[position].Path[0].x, layer.Cst_Crack_List[position].Path[0].y, vPrev)
								path.moveTo(vPrev.x, vPrev.y)
								for (point in layer.Cst_Crack_List[position].Path) {
									sourceToViewCoord(point.x, point.y, vPoint)
									path.quadTo(
										vPrev.x,
										vPrev.y,
										(vPoint.x + vPrev.x) / 2,
										(vPoint.y + vPrev.y) / 2
									)
									vPrev = vPoint
								}
								val radius = (scale * layer.Cst_Crack_List[position].crackLength) * 1f
								canvas.drawCircle(vPrev.x, vPrev.y, radius, linePaint)
							}
							"CRK001" -> {
								path.reset()
								linePaint.color = layer.layerColor
								sourceToViewCoord(layer.Cst_Crack_List[position].Path[0].x, layer.Cst_Crack_List[position].Path[0].y, vPrev)
								path.moveTo(vPrev.x, vPrev.y)
								for (point in layer.Cst_Crack_List[position].Path) {
									sourceToViewCoord(point.x, point.y, vPoint)
									path.quadTo(
										vPrev.x,
										vPrev.y,
										(vPoint.x + vPrev.x) / 2,
										(vPoint.y + vPrev.y) / 2
									)
									vPrev = vPoint
								}
								canvas.drawPath(path, linePaint)
							}
							else -> {
								patternPaint.color = layer.layerColor
								linePaint.color = layerList[layerList.size - 1].layerColor
								val startPoint = sourceToViewCoord(layer.Cst_Crack_List[position].Path[0].x, layer.Cst_Crack_List[position].Path[0].y)
								val endPoint = sourceToViewCoord(layer.Cst_Crack_List[position].Path[layer.Cst_Crack_List[position].Path.size-1].x, layer.Cst_Crack_List[position].Path[layer.Cst_Crack_List[position].Path.size-1].y)
								when(layer.Cst_Crack_List[position].crackType){
									"CRK002" -> {
										patternPaint.shader = getPattern(R.drawable.ic_crk002)
									}
									"CRK003" -> {
										patternPaint.shader = getPattern(R.drawable.ic_crk003)
									}
									"CRK004" -> {
										patternPaint.shader = getPattern(R.drawable.ic_crk004)
									}
									"CRK005" -> {
										patternPaint.shader = getPattern(R.drawable.ic_crk005)
									}
									"CRK006" -> {
										patternPaint.shader = getPattern(R.drawable.ic_crk006)
									}
									"CRK007" -> {
										patternPaint.shader = getPattern(R.drawable.ic_crk007)
									}
									"CRK008" -> {
										patternPaint.shader = getPattern(R.drawable.ic_crk008)
									}
									"CRK009" -> {
										patternPaint.shader = getPattern(R.drawable.ic_crk009)
									}
								}
								if (startPoint != null && endPoint != null) {
									canvas.drawRect(startPoint.x, startPoint.y, endPoint.x, endPoint.y, patternPaint)
									canvas.drawRect(startPoint.x, startPoint.y, endPoint.x, endPoint.y, linePaint)
								}
							}
						}
					}
				}
			}
			if (pointList.size >= 1 && isDraw){
				when(mode){
					"CRK001" -> {
						linePaint.color = layerList[layerList.size - 1].layerColor
						path.reset()
						Log.d("현재 그림 DRAW", "${vPoint.x} + ${vPoint.y}")
						sourceToViewCoord(pointList[0].x, pointList[0].y, vPrev)
						path.moveTo(vPrev.x, vPrev.y)
						for (i in pointList){
							sourceToViewCoord(i.x, i.y, vPoint)
							path.quadTo(vPrev.x, vPrev.y, (vPoint.x + vPrev.x) / 2, (vPoint.y + vPrev.y) / 2)
							vPrev = vPoint
						}
						canvas!!.drawPath(path, linePaint)
					}
					else -> {
						patternPaint.color = layerList[layerList.size - 1].layerColor
						linePaint.color = layerList[layerList.size - 1].layerColor
						path.reset()
						Log.d("현재 그림 DRAW", "${vPoint.x} + ${vPoint.y}")
						val startPoint = PointF()
						val endPoint = PointF()
						sourceToViewCoord(pointList[0].x, pointList[0].y, startPoint)
						sourceToViewCoord(pointList[pointList.size-1].x, pointList[pointList.size-1].y, endPoint)
						path.moveTo(vPrev.x, vPrev.y)
						for (i in pointList){
							sourceToViewCoord(i.x, i.y, vPoint)
							path.quadTo(vPrev.x, vPrev.y, (vPoint.x + vPrev.x) / 2, (vPoint.y + vPrev.y) / 2)
							vPrev = vPoint
						}
						when(mode){
							"CRK002" -> {
								patternPaint.shader = getPattern(R.drawable.ic_crk002)
							}
							"CRK003" -> {
								patternPaint.shader = getPattern(R.drawable.ic_crk003)
							}
							"CRK004" -> {
								patternPaint.shader = getPattern(R.drawable.ic_crk004)
							}
							"CRK005" -> {
								patternPaint.shader = getPattern(R.drawable.ic_crk005)
							}
							"CRK006" -> {
								patternPaint.shader = getPattern(R.drawable.ic_crk006)
							}
							"CRK007" -> {
								patternPaint.shader = getPattern(R.drawable.ic_crk007)
							}
							"CRK008" -> {
								patternPaint.shader = getPattern(R.drawable.ic_crk008)
							}
							"CRK009" -> {
								patternPaint.shader = getPattern(R.drawable.ic_crk009)
							}
						}
						canvas!!.drawRect(startPoint.x, startPoint.y, endPoint.x, endPoint.y, patternPaint)
						canvas.drawRect(startPoint.x, startPoint.y, endPoint.x, endPoint.y, linePaint)
					}
				}
				
			}
		}
	}
	
	@SuppressLint("UseCompatLoadingForDrawables")
	private fun getPattern(svg: Int): BitmapShader {
		val mPatternBitmap = context.getDrawable(svg)!!.toBitmap(130, 70)
		val mPattern = context.getDrawable(svg)!!.toBitmap()
		return BitmapShader(
			mPatternBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT
		)
	}
	
	fun removeAddedCrack(size: Int){
		for (i in  layerList[0].Cst_Crack_List.size - 1 downTo layerList[0].Cst_Crack_List.size - size){
			layerList[0].Cst_Crack_List.removeAt(i)
			(parent as RelativeLayout).removeView((parent as RelativeLayout).findViewWithTag(i))
		}
	}
	
	fun setScale(hei: Int, wth: Int){
		realHeight = hei
		realWidth = wth
	}
	
	fun setLayer(layer: ArrayList<CadLayer>){
		layerList = layer
	}
	
	fun setDrawable(draw : Boolean){
		isDraw = draw
	}
	
	fun setSelectedPosition(pos: Int){
		selectedItem = pos
		invalidate()
	}
	fun clearLayer(){
		layerList.clear()
	}
	fun setLayerVisibility(){
		layerVisible = !layerVisible
		isDraw = !isDraw
		invalidate()
	}
	private fun addCrackToParent(crack: CadCrackItem){
		layerList[layerList.size - 1].Cst_Crack_List.add(crack)
		(context as MainActivity).setCadSelectedPosition(layerList[layerList.size - 1].Cst_Crack_List.size - 1)
		(context as MainActivity).addCadCrackCount()
	}
	fun setNumberingState(){
		numberingState = !numberingState
		invalidate()
	}
	fun setMode(m: String){
		mode = m
	}
	
}