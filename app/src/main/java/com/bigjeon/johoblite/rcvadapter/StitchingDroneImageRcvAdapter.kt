package com.bigjeon.johoblite.rcvadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bigjeon.johoblite.data.DroneImageItem
import com.bigjeon.johoblite.databinding.StitchingImageRcvItemBinding
import com.bigjeon.johoblite.inter.CheckBoxInterface
import com.bumptech.glide.Glide


class StitchingDroneImageRcvAdapter(private val context: Context, var checkBoxInterface: CheckBoxInterface) : RecyclerView.Adapter<StitchingDroneImageRcvAdapter.ViewHolder>() {
	
	private val droneImageList = arrayListOf<DroneImageItem>()
	private var pos = -1
	private lateinit var binding: StitchingImageRcvItemBinding
	private var checkedItem = arrayListOf<DroneImageItem>()
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		binding = StitchingImageRcvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(binding)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		return holder.bind(droneImageList[position], position)
	}
	
	override fun getItemCount(): Int {
		return droneImageList.size
	}
	
	inner class ViewHolder (private val binding: StitchingImageRcvItemBinding) : RecyclerView.ViewHolder(binding.root){
		fun bind(item: DroneImageItem, position: Int){
			binding.uploadImageItemCheckBox.setOnCheckedChangeListener(null)
			if (position == pos){
				binding.container.setBackgroundResource(com.bigjeon.johoblite.R.drawable.default_background_select)
			}else{
				binding.container.setBackgroundResource(com.bigjeon.johoblite.R.drawable.default_background_child)
			}
			binding.uploadImageItemCheckBox.isChecked = item.isChecked
			binding.uploadImageItemCheckBox.setOnClickListener {
				if (binding.uploadImageItemCheckBox.isChecked) {
					checkedItem.add(item)
					droneImageList[position].isChecked = true
					checkBoxInterface.getCount(checkedItem.size)
				} else {
					checkedItem.remove(item)
					droneImageList[position].isChecked = false
					checkBoxInterface.getCount(checkedItem.size)
				}
			}
			binding.droneImageItemName.text = item.fileName
			val circularProgressDrawable = CircularProgressDrawable(context)
			circularProgressDrawable.strokeWidth = 5f
			circularProgressDrawable.centerRadius = 30f
			circularProgressDrawable.start()
			Glide.with(context).load(item.imagePath).placeholder(circularProgressDrawable).into(binding.droneImageItemImageView)
			binding.droneImageItemImageView.setOnClickListener{listener?.onItemClick(binding.droneImageItemImageView, item, item.imagePath, position)}
		}
	}
	
	interface OnItemClickListener{
		fun onItemClick(v: View, data: DroneImageItem, uri: Int, position: Int)
	}
	
	private var listener : OnItemClickListener? = null
	
	fun setOnItemClickListener(listener : OnItemClickListener) {
		this.listener = listener
	}
	@SuppressLint("NotifyDataSetChanged")
	fun changeShowingPosition(p: Int){
		pos = p
		notifyDataSetChanged()
	}
	@SuppressLint("NotifyDataSetChanged")
	fun clearImageList(){
		droneImageList.clear()
		pos = -1
		notifyDataSetChanged()
	}
	@SuppressLint("NotifyDataSetChanged")
	fun addImageList(image: DroneImageItem){
		droneImageList.add(image)
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setSelect(state: Boolean){
		for (i in droneImageList)
			i.isChecked = state
		if (state){
			checkedItem.clear()
			checkedItem.addAll(droneImageList)
		}else{
			checkedItem.clear()
		}
		checkBoxInterface.getCount(checkedItem.size)
		notifyDataSetChanged()
	}
	
	override fun getItemId(position: Int): Long {
		return position.toLong()
	}
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
	
}