package com.bigjeon.johoblite.rcvadapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.Member
import com.bigjeon.johoblite.data.Plane
import com.google.android.material.color.MaterialColors

class PlaneExpandableListAdapter(private val context: Context, private var parentList: MutableList<Member>, private var childList: MutableList<MutableList<Plane>>) : BaseExpandableListAdapter(){
	
	private var selectedParentPosition = -1
	private var selectedChildPosition = -1
	
	override fun getGroupCount(): Int {
		return parentList.size
	}
	
	override fun getChildrenCount(p0: Int): Int {
		return childList[p0].size
	}
	
	override fun getGroup(p0: Int): Any {
		return parentList[p0]
	}
	
	override fun getChild(p0: Int, p1: Int): Any {
		return childList[p0][p1].name
	}
	
	override fun getGroupId(p0: Int): Long {
		return p0.toLong()
	}
	
	override fun getChildId(p0: Int, p1: Int): Long {
		return p1.toLong()
	}
	
	override fun hasStableIds(): Boolean {
		return false
	}
	
	override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
		val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val parentView = inflater.inflate(R.layout.plane_rcv_item, parent, false)
		val parentCategoryIcon = parentView.findViewById<ImageView>(R.id.planeIcon)
		val parentCategoryText = parentView.findViewById<TextView>(R.id.planeNameTv)
		val parentCategoryExpandBtn = parentView.findViewById<ImageView>(R.id.expandControlBtn)
		parentCategoryIcon.background = setIcon(parentList[groupPosition].code)
		parentCategoryText.text = parentList[groupPosition].name
		if(isExpanded) {
			parentCategoryExpandBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
		} else {
			parentCategoryExpandBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
		}
		return parentView
	}
	
	@SuppressLint("UseCompatLoadingForDrawables")
	override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
		val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val childView = inflater.inflate(R.layout.plane_expandable_item, parent, false)
		val childCategoryContainer = childView.findViewById<RelativeLayout>(R.id.planeContainerView)
		val childCategoryTv = childView.findViewById<TextView>(R.id.planeExpandNameTv)
		childCategoryTv.text = getChild(groupPosition, childPosition).toString()
		if (selectedParentPosition == groupPosition && selectedChildPosition == childPosition){
			childCategoryTv.setTextColor(MaterialColors.getColor(context, R.attr.default_color, Color.BLACK))
			childCategoryContainer.background = context.resources.getDrawable(R.drawable.default_subtitle_select_background, context.theme)
		}else{
			childCategoryTv.setTextColor(context.getColor(R.color.secondary_text_color))
			childCategoryContainer.background = null
		}
		return childView
	}
	
	override fun isChildSelectable(p0: Int, p1: Int): Boolean {
		return true
	}
	
	@SuppressLint("UseCompatLoadingForDrawables")
	private fun setIcon(str : String): Drawable{
		when(str){
			context.resources.getString(R.string.MBR001) -> {
				return context.resources.getDrawable(R.drawable.ic_mbr001, null)
			}
			context.resources.getString(R.string.MBR002) -> {
				return context.resources.getDrawable(R.drawable.ic_mbr002, null)
			}
			context.resources.getString(R.string.MBR003) -> {
				return context.resources.getDrawable(R.drawable.ic_mbr003, null)
			}
			context.resources.getString(R.string.MBR004) -> {
				return context.resources.getDrawable(R.drawable.ic_mbr004, null)
			}
			context.resources.getString(R.string.MBR005) -> {
				return context.resources.getDrawable(R.drawable.ic_mbr005, null)
			}
			context.resources.getString(R.string.MBR006) -> {
				return context.resources.getDrawable(R.drawable.ic_mbr008, null)
			}
			context.resources.getString(R.string.MBR007) -> {
				return context.resources.getDrawable(R.drawable.ic_mbr007, null)
			}
			context.resources.getString(R.string.MBR008) -> {
				return context.resources.getDrawable(R.drawable.ic_habu, null)
			}
			context.resources.getString(R.string.MBR009) -> {
				return context.resources.getDrawable(R.drawable.ic_mbr009, null)
			}
			context.resources.getString(R.string.MBR010) -> {
				return context.resources.getDrawable(R.drawable.ic_mbr010, null)
			}
			else -> {
				return context.resources.getDrawable(R.drawable.ic_baseline_close_24, null)
			}
		}
	}
	
	fun changeSelectedParentPosition(){
		notifyDataSetChanged()
	}
	fun changeSelectedChildPosition(parentPos: Int, childPos: Int){
		selectedParentPosition = parentPos
		selectedChildPosition = childPos
		notifyDataSetChanged()
	}
	
	fun clearList(){
		parentList.clear()
		childList.clear()
		selectedChildPosition = -1
		selectedParentPosition = -1
		this.notifyDataSetChanged()
	}
	fun setList(parent: MutableList<Member>, child: MutableList<MutableList<Plane>>){
		parentList = parent
		childList = child
		notifyDataSetChanged()
	}
	
}

