package com.bigjeon.johoblite.rcvadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.databinding.DrawingToolItemBinding

class DrawingToolRcvAdapter(private val context: Context) : RecyclerView.Adapter<DrawingToolRcvAdapter.ViewHolder>(){
	
	private lateinit var binding: DrawingToolItemBinding
	private var selectedPosition = 0
	private val toolList = arrayListOf<String>("NONE", "CRK001", "CRK002", "CRK003", "CRK004", "CRK005", "CRK006", "CRK007", "CRK008", "CRK009")
	
	inner class ViewHolder(private val binding: DrawingToolItemBinding) : RecyclerView.ViewHolder(binding.root) {
		fun bind(item: String, pos: Int){
			when(item){
				"NONE" -> {
					binding.crackTypeImv.setBackgroundResource(R.drawable.default_background_normal)
					binding.crackNameTv.text = "없음"
				}
				"CRK001" -> {
					binding.crackTypeImv.setBackgroundResource(R.drawable.ic_crk001)
					binding.crackNameTv.text = "균열"
				}
				"CRK002" -> {
					binding.crackTypeImv.setBackgroundResource(R.drawable.ic_crk002)
					binding.crackNameTv.text = "망상 균열"
				}
				"CRK003" -> {
					binding.crackTypeImv.setBackgroundResource(R.drawable.ic_crk003)
					binding.crackNameTv.text = "박리, 들뜸"
				}
				"CRK004" -> {
					binding.crackTypeImv.setBackgroundResource(R.drawable.ic_crk004)
					binding.crackNameTv.text = "누수, 습운부"
				}
				"CRK005" -> {
					binding.crackTypeImv.setBackgroundResource(R.drawable.ic_crk005)
					binding.crackNameTv.text = "표면 부식"
				}
				"CRK006" -> {
					binding.crackTypeImv.setBackgroundResource(R.drawable.ic_crk006)
					binding.crackNameTv.text = "박락, 파손"
				}
				"CRK007" -> {
					binding.crackTypeImv.setBackgroundResource(R.drawable.ic_crk007)
					binding.crackNameTv.text = "철근 노출"
				}
				"CRK008" -> {
					binding.crackTypeImv.setBackgroundResource(R.drawable.ic_crk008)
					binding.crackNameTv.text = "재료 분리"
				}
				"CRK009" -> {
					binding.crackTypeImv.setBackgroundResource(R.drawable.ic_crk009)
					binding.crackNameTv.text = "백태"
				}
			}
			if (item == toolList[selectedPosition]){
				binding.container.setBackgroundResource(R.drawable.default_background_select)
				binding.crackNameTv.setBackgroundResource(R.drawable.default_background_normal)
			}else{
				binding.container.setBackgroundResource(R.drawable.default_background_child)
				binding.crackNameTv.setBackgroundResource(R.drawable.default_background_normal)
			}
			
			binding.container.setOnClickListener { listener?.onItemClick(binding.container, item, pos) }
			
		}
	}
	
	interface OnItemClickListener{
		fun onItemClick(v: View, mode: String, position: Int)
	}
	
	private var listener : OnItemClickListener? = null
	
	fun setOnItemClickListener(listener : DrawingToolRcvAdapter.OnItemClickListener) {
		this.listener = listener
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun changePosition(p: Int){
		selectedPosition = p
		notifyDataSetChanged()
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		binding = DrawingToolItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(binding)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		return holder.bind(toolList[position], position)
	}
	
	override fun getItemCount(): Int {
		return toolList.size
	}
	
}