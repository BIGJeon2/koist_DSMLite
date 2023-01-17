package com.bigjeon.johoblite.service

import com.bigjeon.johoblite.data.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*


interface OcrFactory {
    
    //DB에서 가져올 DATA 기능 모음
    @Headers("Content-Type: application/json")
    @POST("/user/signin.do")
    fun postUserCheck(@Body user: PostUserModel): Call<JsonObject>
    
    @GET("/user/signout.do")
    fun signOut(): Call<JsonObject>
    
    @GET("/system/streaming-tutorial-video.do")
    fun getTutorialVideo(): Call<JsonObject>
    
    @GET("/user/get-signin-info.do")
    fun getUserSignInInfo(): Call<PostUserResult>
    
    @GET("/user/get-user-theme-info.do")
    fun getUserThemeInInfo(): Call<JsonObject>
    
    @GET("/user/update-user-theme.do")
    fun setUserTheme(@Query("id") id: String, @Query("theme") theme: String): Call<JsonObject>
    
    @Headers("Content-Type: application/json")
    @POST("/user/chg-pw.do")
    fun setUserPassword(@Body changePw: ChangePw): Call<JsonObject>
    
    @GET("/facility-info/get-facility-list.do")
    fun getFacilityList(@Query("bounds") bounds: String): Call<JsonObject>
    
    @GET("/facility-info/search-facility.do")
    fun getSearchedCstFacilityList(@Query("inputValue") name: String): Call<JsonObject>
    
    @GET("/facility-info/get-facility-info.do")
    fun getSelectedFacilityInfo(@Query("name") name: String): Call<JsonObject>
    
    @GET("/system/get-facil-list.do")
    fun getCstFacilityList(): Call<JsonObject>
    
    @GET("/system/get-project-list-and-facil-info.do")
    fun getCstProjectList(@Query("facilCode") string: String) : Call<JsonObject>
    
    @GET("/system/get-range.do")
    fun getCstKeyPlanList(@Query("path") string: String) : Call<JsonObject>
    
    @GET("/system/get-member-list.do")
    fun getCstPlaneList(@Query("path") path: String) : Call<JsonObject>
    
    @GET("/upload/get-section-dir-list.do")
    fun getCstSectionList(@Query("path") path: String) : Call<JsonObject>
    
    @GET("/analysis/get-drone-image-list-with-attr.do")
    fun getCstDroneImageList(@Query("path") path: String, @Query("type") type: String) : Call<JsonObject>
    
    @GET("/analysis/m.get-stitched-image-info.do")
    fun getCstStitchedImage(@Query("path") path: String, @Query("type") type: String) : Call<JsonObject>
    
    @GET("/analysis/get-stitched-detection-info.do")
    fun getCstCrackLayer(@Query("path") path: String) : Call<JsonObject>
    
}