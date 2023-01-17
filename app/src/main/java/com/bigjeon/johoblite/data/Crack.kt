package com.bigjeon.johoblite.data

import android.graphics.PointF

data class Crack (
	val crackId: Int,
	var crackType: String,
	var crackWidth: Float,
	var crackLength: Float,
	val Path: ArrayList<PointF>,
	var repairState: Boolean,
	var note: String)