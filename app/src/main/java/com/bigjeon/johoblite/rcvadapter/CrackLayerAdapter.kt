package com.bigjeon.johoblite.rcvadapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.DetectionCrack
import com.bigjeon.johoblite.databinding.DetectionLayerItemBinding
import com.google.android.material.color.MaterialColors
import kotlin.math.roundToInt

class CrackLayerAdapter(private val context: Context) : RecyclerView.Adapter<CrackLayerAdapter.ViewHolder>(){
	
	private lateinit var binding: DetectionLayerItemBinding
	private val crackList = arrayListOf<DetectionCrack>()
	private var selectedPosition = -1
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		binding = DetectionLayerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(binding)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		return holder.bind(crackList[position], position)
	}
	
	override fun getItemCount(): Int {
		return crackList.size
	}
	
	inner class ViewHolder(private val binding: DetectionLayerItemBinding): RecyclerView.ViewHolder(binding.root) {
		@SuppressLint("SetTextI18n")
		fun bind(item: DetectionCrack, position: Int){
			binding.viewContainer.setOnClickListener{listener?.onItemClick(binding.viewContainer, position)}
			if (selectedPosition == position){
				binding.detectionLayerNumTv.setBackgroundColor(MaterialColors.getColor(context, R.attr.transparent_default_color, Color.BLACK))
				binding.detectionLayerSizeTv.setBackgroundColor(MaterialColors.getColor(context, R.attr.transparent_default_color, Color.BLACK))
				binding.detectionLayerNameTv.setBackgroundColor(MaterialColors.getColor(context, R.attr.transparent_default_color, Color.BLACK))
				binding.detectionLayerAreaTv.setBackgroundColor(MaterialColors.getColor(context, R.attr.transparent_default_color, Color.BLACK))
			}else{
				binding.detectionLayerNumTv.setBackgroundColor(context.getColor(R.color.TRANSPARENT))
				binding.detectionLayerSizeTv.setBackgroundColor(context.getColor(R.color.TRANSPARENT))
				binding.detectionLayerNameTv.setBackgroundColor(context.getColor(R.color.TRANSPARENT))
				binding.detectionLayerAreaTv.setBackgroundColor(context.getColor(R.color.TRANSPARENT))
			}
			with(binding){
				detectionLayerNumTv.text = (position + 1).toString()
				detectionLayerSizeTv.text = item.crackLength.toString() + "*" + item.crackWidth.toString()
				detectionLayerAreaTv.text = ((item.crackWidth * item.crackLength * 100).roundToInt() / 100.0).toString()
				when(item.crackType){
					"CRK001" -> {
						detectionLayerNameTv.text = "Crack"
						detectionLayerTypeImageview.setBackgroundResource(R.drawable.ic_crk001)
					}
					"CRK002" -> {
						detectionLayerNameTv.text = "망상균열"
						detectionLayerTypeImageview.setBackgroundResource(R.drawable.ic_crk001)
					}
					"CRK003" -> {
						detectionLayerNameTv.text = "박리,들뜸"
						detectionLayerTypeImageview.setBackgroundResource(R.drawable.ic_crk001)
					}
					"CRK004" -> {
						detectionLayerNameTv.text = "누수,습운부"
						detectionLayerTypeImageview.setBackgroundResource(R.drawable.ic_crk001)
					}
					"CRK005" -> {
						detectionLayerNameTv.text = "강재표면 부식"
						detectionLayerTypeImageview.setBackgroundResource(R.drawable.ic_crk001)
					}
					"CRK006" -> {
						detectionLayerNameTv.text = "박락,파손"
						detectionLayerTypeImageview.setBackgroundResource(R.drawable.ic_crk001)
					}
					"CRK007" -> {
						detectionLayerNameTv.text = "철근 노출"
						detectionLayerTypeImageview.setBackgroundResource(R.drawable.ic_crk001)
					}
					"CRK008" -> {
						detectionLayerNameTv.text = "재료 분리"
						detectionLayerTypeImageview.setBackgroundResource(R.drawable.ic_crk001)
					}
					"CRK009" -> {
						detectionLayerNameTv.text = "백태"
						detectionLayerTypeImageview.setBackgroundResource(R.drawable.ic_crk001)
					}
				}
			}
		}
	}
	
	interface OnItemClickListener{
		fun onItemClick(v: View, position: Int)
	}
	
	private var listener : OnItemClickListener? = null
	
	fun setOnItemClickListener(listener : OnItemClickListener) {
		this.listener = listener
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setSelectedPosition(pos: Int){
		selectedPosition = pos
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun addCrack(crack: DetectionCrack){
		crackList.add(crack)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun clearLayerList(){
		Log.d("LAYERLISTCLEAR", "CLEAR!@!!!!")
		crackList.clear()
		notifyDataSetChanged()
	}
	
}