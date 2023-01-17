package com.bigjeon.johoblite.rcvadapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.PlaneProgressItem
import com.google.android.material.color.MaterialColors

class PlaneProgressRcvAdapter(private val context: Context, private val planeList: ArrayList<PlaneProgressItem>) : RecyclerView.Adapter<PlaneProgressRcvAdapter.ViewHolder>() {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.progress_expandable_list_item, parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(planeList[position])
	}
	
	override fun getItemCount(): Int {
		return planeList.size
	}
	
	inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
		@SuppressLint("UseCompatLoadingForDrawables")
		fun bind(item: PlaneProgressItem){
			val childProgressPlaneName = itemView.findViewById<TextView>(R.id.progressPlaneNameTv)
			val childUploadTv = itemView.findViewById<TextView>(R.id.progressExpandUploadTv)
			val childUploadInspectionTv = itemView.findViewById<TextView>(R.id.progressExpandUploadInspectionTv)
			val childStitchingTv = itemView.findViewById<TextView>(R.id.progressExpandStitchingTv)
			val childStitchingInspectionTv = itemView.findViewById<TextView>(R.id.progressExpandStitchingInspectionTv)
			val childDetectionTv = itemView.findViewById<TextView>(R.id.progressExpandAnalysisTv)
			val childDetectionInspectionTv = itemView.findViewById<TextView>(R.id.progressExpandAnalysisInspectionTv)
			val disableReasonBtn = itemView.findViewById<TextView>(R.id.disableReasonBtn)
			childProgressPlaneName.text = item.planeName
			when(item.planeProgress){
				0 -> {
					childUploadTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_left_progress, context.theme)
					childUploadInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childStitchingTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childStitchingInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childDetectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childDetectionInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_right_off, context.theme)
					childUploadTv.setTextColor(context.resources.getColor(R.color.white, null))
					childUploadInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					childStitchingTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					childStitchingInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					childDetectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					childDetectionInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
				}
				1 -> {
					childUploadTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_left_on, context.theme)
					childUploadInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_progress, context.theme)
					childStitchingTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childStitchingInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childDetectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childDetectionInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_right_off, context.theme)
					childUploadTv.setTextColor(context.resources.getColor(R.color.white, null))
					childUploadInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					childStitchingInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					childDetectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					childDetectionInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
				}
				2 -> {
					childUploadTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_left_on, context.theme)
					childUploadInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childStitchingTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_progress, context.theme)
					childStitchingInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childDetectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childDetectionInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_right_off, context.theme)
					childUploadTv.setTextColor(context.resources.getColor(R.color.white, null))
					childUploadInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					childDetectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					childDetectionInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
				}
				3 -> {
					childUploadTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_left_on, context.theme)
					childUploadInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childStitchingTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childStitchingInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_progress, context.theme)
					childDetectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childDetectionInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_right_off, context.theme)
					childUploadTv.setTextColor(context.resources.getColor(R.color.white, null))
					childUploadInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childDetectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					childDetectionInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
				}
				4 -> {
					childUploadTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_left_on, context.theme)
					childUploadInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childStitchingTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childStitchingInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childDetectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_progress, context.theme)
					childDetectionInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_right_off, context.theme)
					childUploadTv.setTextColor(context.resources.getColor(R.color.white, null))
					childUploadInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childDetectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childDetectionInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
				}
				5 -> {
					childUploadTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_left_on, context.theme)
					childUploadInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childStitchingTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childStitchingInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childDetectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childDetectionInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_right_progress, context.theme)
					childUploadTv.setTextColor(context.resources.getColor(R.color.white, null))
					childUploadInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childDetectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childDetectionInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
				}
				6 -> {
					childUploadTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_left_on, context.theme)
					childUploadInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childStitchingTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childStitchingInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childDetectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_on, context.theme)
					childDetectionInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_right_on, context.theme)
					childUploadTv.setTextColor(context.resources.getColor(R.color.white, null))
					childUploadInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingTv.setTextColor(context.resources.getColor(R.color.white, null))
					childStitchingInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childDetectionTv.setTextColor(context.resources.getColor(R.color.white, null))
					childDetectionInspectionTv.setTextColor(context.resources.getColor(R.color.white, null))
				}
				7 -> {
					childUploadTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_left_off, context.theme)
					childUploadInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childStitchingTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childStitchingInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childDetectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_middle_off, context.theme)
					childDetectionInspectionTv.background = context.resources.getDrawable(R.drawable.progress_rcv_background_right_off, context.theme)
					childUploadTv.setTextColor(context.resources.getColor(R.color.disable_color, null))
					childUploadInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.disable_color, Color.BLACK))
					childStitchingTv.setTextColor(MaterialColors.getColor(context, R.attr.disable_color, Color.BLACK))
					childStitchingInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.disable_color, Color.BLACK))
					childDetectionTv.setTextColor(MaterialColors.getColor(context, R.attr.disable_color, Color.BLACK))
					childDetectionInspectionTv.setTextColor(MaterialColors.getColor(context, R.attr.disable_color, Color.BLACK))
					disableReasonBtn.visibility = View.VISIBLE
				}
			}
		}
	}
	
	override fun getItemViewType(position: Int): Int {
		return position
	}
}
