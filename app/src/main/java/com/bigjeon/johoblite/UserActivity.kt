package com.bigjeon.johoblite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.bigjeon.johoblite.databinding.ActivityUserBinding
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.pageadapter.UserActivityPageAdapter
import com.bigjeon.johoblite.service.OcrFactory

class UserActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityUserBinding
	private lateinit var pageAdapter : UserActivityPageAdapter
	
	//retrofit2
	private lateinit var serviceClient: OcrFactory
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
		
		when(sharedPreference.getInt("USER_THEME", 0)){
			0 -> setTheme(R.style.Theme_Komapperlite_default)
			1 -> setTheme(R.style.Theme_Komapperlite_orange)
			2 -> setTheme(R.style.Theme_Komapperlite_green)
			3 -> setTheme(R.style.Theme_Komapperlite_purple)
			4 -> setTheme(R.style.Theme_Komapperlite_brown)
		}
		binding = ActivityUserBinding.inflate(layoutInflater)
		
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		
		pageAdapter = UserActivityPageAdapter(this)
		binding.userInfoViewPager.adapter = pageAdapter
		binding.userInfoViewPager.offscreenPageLimit = 1
		binding.userInfoViewPager.registerOnPageChangeCallback(object :
			ViewPager2.OnPageChangeCallback() {
			override fun onPageSelected(position: Int) {
				changeFragment(position)
				super.onPageSelected(position)
			}
		})
		if (sharedPreference.getBoolean("USER_ADMIN", false)){
			binding.isAdminTV.text = "Grade : Administrator"
		}else{
			binding.isAdminTV.text = "Grade : Normal User"
		}
		
		
		binding.basicInfoBtnContainer.setOnClickListener {
			changeFragment(0)
		}
		binding.passwordBtnContainer.setOnClickListener {
			changeFragment(1)
		}
		binding.themeBtnContainer.setOnClickListener {
			changeFragment(2)
		}
		binding.signOutBtn.setOnClickListener {
			signOut()
		}
		setContentView(binding.root)
	}
	
	private fun signOut(){
		/*CoroutineScope(Dispatchers.IO).launch {
			serviceClient.signOut().enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
					Toast.makeText(applicationContext, "정상적으로 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
				}
				
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
				}
			})
		}*/
		val intent = Intent(this, LoginActivity::class.java)
		startActivity(intent)
		ActivityCompat.finishAffinity(this)
	}
	
	private fun changeFragment(state: Int){
		val typedValue = TypedValue()
		theme.resolveAttribute(R.attr.default_color, typedValue, true)
		when(state){
			0 ->{
				binding.point1.setImageResource(typedValue.resourceId)
				binding.point2.setImageResource(R.color.secondary_text_color)
				binding.point3.setImageResource(R.color.secondary_text_color)
				binding.basicInfoBtn.setTextColor(this.getColor(typedValue.resourceId))
				binding.passwordBtn.setTextColor(this.getColor(R.color.secondary_text_color))
				binding.themeBtn.setTextColor(this.getColor(R.color.secondary_text_color))
			}
			1 -> {
				binding.point1.setImageResource(R.color.secondary_text_color)
				binding.point2.setImageResource(typedValue.resourceId)
				binding.point3.setImageResource(R.color.secondary_text_color)
				binding.basicInfoBtn.setTextColor(this.getColor(R.color.secondary_text_color))
				binding.passwordBtn.setTextColor(this.getColor(typedValue.resourceId))
				binding.themeBtn.setTextColor(this.getColor(R.color.secondary_text_color))
			}
			2 -> {
				binding.point1.setImageResource(R.color.secondary_text_color)
				binding.point2.setImageResource(R.color.secondary_text_color)
				binding.point3.setImageResource(typedValue.resourceId)
				binding.basicInfoBtn.setTextColor(this.getColor(R.color.secondary_text_color))
				binding.passwordBtn.setTextColor(this.getColor(R.color.secondary_text_color))
				binding.themeBtn.setTextColor(this.getColor(typedValue.resourceId))
			}
		}
		pageAdapter.createFragment(state)
		binding.userInfoViewPager.currentItem = state
	}
	
}