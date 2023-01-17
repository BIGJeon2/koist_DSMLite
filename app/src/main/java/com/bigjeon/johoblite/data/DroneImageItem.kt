package com.bigjeon.johoblite.data

data class DroneImageItem (
	var fileName: String,
	var imagePath: Int,
	var uploader: String,
	var date: String,
	var note: String?,
	var isChecked: Boolean
	)