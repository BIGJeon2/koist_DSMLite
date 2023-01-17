package com.bigjeon.johoblite.rcvadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.databinding.ScaleProgressItemBinding

class ScaleRcvAdapter(private val context: Context) : RecyclerView.Adapter<ScaleRcvAdapter.ViewHolder>(){
	
	private val sectionList = arrayListOf<String>()
	private lateinit var binding: ScaleProgressItemBinding
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.scale_progress_item, parent, false)
		return ViewHolder(binding)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(sectionList[position])
	}
	
	override fun getItemCount(): Int {
		return sectionList.size
	}
	
	inner class ViewHolder (val binding: ScaleProgressItemBinding) :RecyclerView.ViewHolder(binding.root){
		@SuppressLint("UseCompatLoadingForDrawables")
		fun bind(item: String){
			binding.scaleSectionNameTv.text = item
			binding.scaleProgressLayout.background = context.getDrawable(R.drawable.default_background_child)
			binding.scaleProgressImageView.background = context.getDrawable(R.drawable.ic_baseline_check_24)
		}
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun addSectionList(item: String){
		sectionList.add(item)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun clearList(){
		sectionList.clear()
		notifyDataSetChanged()
	}
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
	
}
