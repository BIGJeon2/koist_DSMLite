package com.bigjeon.johoblite.data


data class DetectionCrack (
	val crackId: Int,
	var crackType: String,
	var crackWidth: Float,
	var crackLength: Float,
	var repairState: Boolean,
	var note: String)