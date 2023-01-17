package com.bigjeon.johoblite.data

import com.google.gson.annotations.SerializedName

data class User(
	@SerializedName("id")
	val id : String,
	@SerializedName("name")
	val name : String,
	@SerializedName("isAdmin")
	val is_admin : Boolean
)

data class PostUserModel(
	var id : String,
	var password: String
)

data class PostUserResult(
	@SerializedName("user")
	var user: User,
	@SerializedName("result")
	var result: Boolean,
	@SerializedName("isSignin")
	var isSignin: Boolean,
	@SerializedName("sessionID")
	var sessionID: String
)
