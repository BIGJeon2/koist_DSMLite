package com.bigjeon.johoblite.data

import android.graphics.PointF

data class CadCrackItem(
	val crackId: Int,
	var crackType: String,
	var crackWidth: Float,
	var crackLength: Float,
	var Path: ArrayList<PointF>,
	var repairState: Boolean,
	var note: String,
	var isShape: Boolean,
	var insert : Boolean,
	//0 -> left 1 -> center 2 -> right
	var allign: Int)