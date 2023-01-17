package com.bigjeon.johoblite.data

data class ProgressItem(
	val divideName: String,
	val divideProgressing: Int,
	val planeProgressList: ArrayList<PlaneProgressItem>,
	var isExpanded: Boolean,
	var impossible: Boolean
)
