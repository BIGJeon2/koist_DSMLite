package com.bigjeon.johoblite.rcvadapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.KeyPlan
import com.bigjeon.johoblite.databinding.KeyplanItemBinding

class KeyPlanRcvAdapter : RecyclerView.Adapter<KeyPlanRcvAdapter.ViewHolder>() {
	
	private var keyPlanList = arrayListOf<KeyPlan>()
	private lateinit var binding: KeyplanItemBinding
	private var showingPosition = "NONE"
	
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): KeyPlanRcvAdapter.ViewHolder {
		binding = KeyplanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(binding)
	}
	
	override fun onBindViewHolder(holder: KeyPlanRcvAdapter.ViewHolder, position: Int) {
		return holder.bind(keyPlanList[position].name)
	}
	
	override fun getItemCount(): Int {
		return keyPlanList.size
	}
	
	inner class ViewHolder(private val binding: KeyplanItemBinding) : RecyclerView.ViewHolder(binding.root) {
		fun bind(item: String) {
			binding.KeyPlanNameTv.text = "수조"
			binding.keyPlanImageView.setOnClickListener{listener?.onItemClick(binding.KeyPlanNameTv, item)}
			if (item == showingPosition){
				binding.keyPlanContainer.setBackgroundResource(R.drawable.default_subtitle_select_background)
			}else{
				binding.keyPlanContainer.setBackgroundResource(0)
				if (item.contains("P")){
					binding.keyPlanImageView.setBackgroundResource(R.drawable.ic_keyplan_p_default)
				}else{
					binding.keyPlanImageView.setBackgroundResource(R.drawable.ic_keyplan_s_disable)
				}
			}
		}
	}

	interface OnItemClickListener{
		fun onItemClick(v: View, data: String)
	}

	private var listener : OnItemClickListener? = null

	fun setOnItemClickListener(listener : OnItemClickListener) {
		this.listener = listener
	}

	@SuppressLint("NotifyDataSetChanged")
	fun clearList(){
		keyPlanList.clear()
		notifyDataSetChanged()
	}
	@SuppressLint("NotifyDataSetChanged")
	fun changeList(list: ArrayList<KeyPlan>){
		keyPlanList = list
		showingPosition = "NONE"
		notifyDataSetChanged()
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun setShowingPosition(str: String){
		showingPosition = str
		notifyDataSetChanged()
	}
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
	
}