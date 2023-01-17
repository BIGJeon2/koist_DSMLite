package com.bigjeon.johoblite.userpage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.databinding.FragmentThemeBinding
import com.bigjeon.johoblite.library.ApiClient
import com.google.android.material.color.MaterialColors
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response


class ThemeFragment : Fragment() {
	
	private lateinit var binding: FragmentThemeBinding
	private lateinit var sharedPreference: SharedPreferences
	private var adjustedTheme = 0
	private var selectedTheme = 0
	
	//retrofit2
	private lateinit var serviceClient: OcrFactory
	
	@SuppressLint("CommitPrefEdits")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentThemeBinding.inflate(inflater, container, false)
		sharedPreference =  requireContext().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
		
		val editor = sharedPreference.edit()
		
		adjustedTheme = sharedPreference.getInt("USER_THEME", 0)
		selectedTheme = sharedPreference.getInt("USER_THEME", 0)
		changePreview(adjustedTheme)
		
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		
		binding.defaultColorBtn.setOnClickListener { setSelectedTheme(0) }
		binding.orangeColorBtn.setOnClickListener { setSelectedTheme(1) }
		binding.greenColorBtn.setOnClickListener { setSelectedTheme(2) }
		binding.purpleColorBtn.setOnClickListener { setSelectedTheme(3) }
		binding.brownColorBtn.setOnClickListener { setSelectedTheme(4) }
		
		binding.saveBtn.setOnClickListener {
			if (selectedTheme != adjustedTheme){
				editor.putInt("USER_THEME", selectedTheme).apply()
				when(selectedTheme){
					0 -> activity?.setTheme(R.style.Theme_Komapperlite_default)
					1 -> activity?.setTheme(R.style.Theme_Komapperlite_orange)
					2 -> activity?.setTheme(R.style.Theme_Komapperlite_green)
					3 -> activity?.setTheme(R.style.Theme_Komapperlite_purple)
					4 -> activity?.setTheme(R.style.Theme_Komapperlite_brown)
				}
				adjustedTheme = selectedTheme
				binding.saveBtn.setBackgroundColor(requireContext().getColor(R.color.disable_color))
				setUserThemeDB()
				Toast.makeText(requireContext(), "테마 변경이 완료되었습니다.(변경된 테마는 어플 재 실행시 적용 됩니다.)", Toast.LENGTH_SHORT).show()
				
/*
				Toast.makeText(requireContext(), "테마 적용이 완료되었습니다.", Toast.LENGTH_SHORT).show()
*/
				activity?.recreate()
			}
		}
		
		return binding.root
	}
	
	private fun changePreview(state: Int){
		binding.defaultColorBtn.borderColor = requireContext().getColor(R.color.white)
		binding.orangeColorBtn.borderColor = requireContext().getColor(R.color.white)
		binding.greenColorBtn.borderColor = requireContext().getColor(R.color.white)
		binding.purpleColorBtn.borderColor = requireContext().getColor(R.color.white)
		binding.brownColorBtn.borderColor = requireContext().getColor(R.color.white)
		when(state){
			0 -> {
				binding.defaultColorBtn.borderColor = requireContext().getColor(R.color.default_border_color)
				binding.previewIMV.setImageResource(R.drawable.theme_preview_default)
			}
			1 -> {
				binding.orangeColorBtn.borderColor = requireContext().getColor(R.color.default_border_color)
				binding.previewIMV.setImageResource(R.drawable.theme_preview_orange)
			}
			2 -> {
				binding.greenColorBtn.borderColor = requireContext().getColor(R.color.default_border_color)
				binding.previewIMV.setImageResource(R.drawable.theme_preview_green)
			}
			3 -> {
				binding.purpleColorBtn.borderColor = requireContext().getColor(R.color.default_border_color)
				binding.previewIMV.setImageResource(R.drawable.theme_preview_purple)
			}
			4 -> {
				binding.brownColorBtn.borderColor = requireContext().getColor(R.color.default_border_color)
				binding.previewIMV.setImageResource(R.drawable.theme_preview_brown)
			}
		}
	}
	 private fun setSelectedTheme(position: Int){
		 selectedTheme = position
		 if (selectedTheme != adjustedTheme){
			 binding.saveBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
		 }else{
			 binding.saveBtn.setBackgroundColor(requireContext().getColor(R.color.disabled_text_color))
		 }
		 changePreview(position)
	 }
	
	private fun setUserThemeDB(){
		
		var themeDB = ""
		
		when(selectedTheme){
			0 -> themeDB = "navy"
			1 -> themeDB = "orange"
			2 -> themeDB = "green"
			3 -> themeDB = "purple"
			4 -> themeDB = "brown"
		}
		CoroutineScope(Dispatchers.IO).launch {
			serviceClient.setUserTheme(sharedPreference.getString("USER_ID", "NULL")!!, themeDB).enqueue(object : retrofit2.Callback<JsonObject> {
				override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
				}
				
				override fun onFailure(call: Call<JsonObject>, t: Throwable) {
				}
			})
		}
	}
	
}