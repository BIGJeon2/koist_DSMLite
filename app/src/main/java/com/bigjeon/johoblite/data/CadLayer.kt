package com.bigjeon.johoblite.data

data class CadLayer(
	//Name
	val Cst_Layer_Name: String,
	val layerColor: Int,
	//Crack_List
	var Cst_Crack_List: ArrayList<CadCrackItem>
)