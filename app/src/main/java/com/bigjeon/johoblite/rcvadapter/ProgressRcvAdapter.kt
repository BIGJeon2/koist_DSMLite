package com.bigjeon.johoblite.rcvadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.anim.ToggleAnimation
import com.bigjeon.johoblite.data.ProgressItem
import com.skydoves.progressview.ProgressView

class ProgressRcvAdapter(private val context: Context, private var divideList: ArrayList<ProgressItem>) : RecyclerView.Adapter<ProgressRcvAdapter.ViewHolder>() {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.progress_rcv_item, parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(divideList[position], position)
		
	}
	
	override fun getItemCount(): Int {
		return divideList.size
	}
	
	inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
		fun bind(item: ProgressItem, position: Int){
			val progressExpandBtn = itemView.findViewById<RelativeLayout>(R.id.progressExpandBtn)
			val progressDivideTv = itemView.findViewById<TextView>(R.id.progressDivideTv)
			val progressDivideProgressBar = itemView.findViewById<ProgressView>(R.id.progressDivideProgressBar)
			val progressPlaneRcv = itemView.findViewById<RecyclerView>(R.id.progressPlaneRcv)
			val expandBtn = itemView.findViewById<ImageView>(R.id.expandBtn)
			val progressPlaneRcvContainer = itemView.findViewById<LinearLayout>(R.id.progressPlaneRcvContainer)
			
			progressExpandBtn.setOnClickListener {
				val show = toggleLayout(!item.isExpanded, expandBtn, progressPlaneRcvContainer)
				divideList[position].isExpanded = show
			}
			progressDivideTv.text = item.divideName
			progressDivideProgressBar.progress = item.divideProgressing.toFloat()
			if (item.divideProgressing.toFloat() == 100f){
				progressDivideProgressBar.labelText = "Completed"
			}else{
				progressDivideProgressBar.labelText = item.divideProgressing.toString() + "%"
			}
			val adapter = PlaneProgressRcvAdapter(context, item.planeProgressList)
			val lm = LinearLayoutManager(context).also { it.orientation = LinearLayoutManager.VERTICAL }
			progressPlaneRcv.adapter = adapter
			progressPlaneRcv.setHasFixedSize(false)
			progressPlaneRcv.layoutManager = lm
		}
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
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
	
	@SuppressLint("NotifyDataSetChanged")
	fun clearList(){
		divideList.clear()
		notifyDataSetChanged()
	}
	@SuppressLint("NotifyDataSetChanged")
	fun addItem(item : ProgressItem){
		divideList.add(item)
		notifyDataSetChanged()
	}
}