package com.bigjeon.johoblite.data

import com.google.gson.annotations.SerializedName

data class Region(
	@SerializedName("east") val east: Double,
	@SerializedName("west") val west: Double,
	@SerializedName("north") val north: Double,
	@SerializedName("south") val south: Double,
	)