package com.bigjeon.johoblite.rcvadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.DroneImageItem
import com.bigjeon.johoblite.databinding.UploadImageItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class UploadDroneImageRcvAdapter(private val context: Context) : RecyclerView.Adapter<UploadDroneImageRcvAdapter.ViewHolder>() {
	
	private val sectionDroneList = arrayListOf<DroneImageItem>()
	private lateinit var binding: UploadImageItemBinding
	private var pos = -1
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		binding = UploadImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(binding)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		return holder.bind(sectionDroneList[position], position)
	}
	
	override fun getItemCount(): Int {
		return sectionDroneList.size
	}
	
	inner class ViewHolder (private val binding: UploadImageItemBinding) : RecyclerView.ViewHolder(binding.root){
		fun bind(item: DroneImageItem, position: Int){
			if (position == pos){
				binding.container.setBackgroundResource(R.drawable.default_background_select)
			}else{
				binding.container.setBackgroundResource(R.drawable.default_background_child)
			}
			binding.droneImageNameTv.text = item.fileName
			val circularProgressDrawable = CircularProgressDrawable(context)
			circularProgressDrawable.strokeWidth = 5f
			circularProgressDrawable.centerRadius = 30f
			circularProgressDrawable.start()
			Glide.with(context).load(item.imagePath).placeholder(circularProgressDrawable).diskCacheStrategy(
				DiskCacheStrategy.NONE).skipMemoryCache(false).into(binding.droneImageView)
			/*Glide.with(context).load(context.getString(R.string.URL) +  "data/" + item.imagePath + context.getString(
				com.bigjeon.komapperlite.R.string.temp) + item.fileName).placeholder(circularProgressDrawable).diskCacheStrategy(
				DiskCacheStrategy.NONE).skipMemoryCache(false).into(binding.droneImageView)*/
			binding.droneImageView.setOnClickListener{listener?.onItemClick(binding.droneImageView, item, item.imagePath, position)}
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
	/*@SuppressLint("NotifyDataSetChanged")
	fun showingPositionPlus(): String{
		if (pos < sectionDroneList.size - 1){
			pos += 1
			notifyDataSetChanged()
		}
		return context.getString(R.string.URL) +  "data/" + sectionDroneList[pos].imagePath + "/" + sectionDroneList[pos].fileName
	}
	@SuppressLint("NotifyDataSetChanged")
	fun showingPositionMinus(): String{
		if (pos > 0){
			pos -= 1
			notifyDataSetChanged()
		}
		return context.getString(R.string.URL) +  "data/" + sectionDroneList[pos].imagePath + "/" + sectionDroneList[pos].fileName
	}*/
	@SuppressLint("NotifyDataSetChanged")
	fun clearImageList(){
		sectionDroneList.clear()
		pos = -1
		notifyDataSetChanged()
	}
	@SuppressLint("NotifyDataSetChanged")
	fun addImageList(image: DroneImageItem){
		sectionDroneList.add(image)
		notifyDataSetChanged()
	}
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
}