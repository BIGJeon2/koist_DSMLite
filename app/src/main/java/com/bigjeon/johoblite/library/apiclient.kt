package com.bigjeon.johoblite.library

import android.util.Log
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.util.concurrent.TimeUnit

object ApiClient {
	private const val URL = "http://101.101.216.123:5009/"
	
	private var builder = OkHttpClient().newBuilder()
	
	private var okHttpClient = builder
		.cookieJar(JavaNetCookieJar(CookieManager()))
		.connectTimeout(15, TimeUnit.SECONDS)
		.writeTimeout(15, TimeUnit.SECONDS)
		.readTimeout(15, TimeUnit.SECONDS)
		.addInterceptor(httpLoggingInterceptor())
		.build()
	
	val retrofit: Retrofit = Retrofit.Builder()
		.baseUrl(URL)
		.client(okHttpClient)
		.addConverterFactory(GsonConverterFactory.create())
		.build()
	
	private fun httpLoggingInterceptor() : HttpLoggingInterceptor {
		val interceptor = HttpLoggingInterceptor { message -> Log.e("DatabaseReceive :", message + "") }
		return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
	}
}