package com.bigjeon.johoblite.userpage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bigjeon.johoblite.databinding.FragmentBasicInfoBinding

class BasicInfoFragment : Fragment() {
	
	private lateinit var binding: FragmentBasicInfoBinding
	private lateinit var sharedPreference: SharedPreferences
	
	@SuppressLint("CommitPrefEdits")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentBasicInfoBinding.inflate(inflater, container, false)
		sharedPreference =  requireContext().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
		var editor = sharedPreference.edit()
		
		binding.userNameTv.text = sharedPreference.getString("USER_NAME", "NONE")
		binding.emailAddressTv.text = "gildong-Hong@gamil.com"
		binding.phoneNumTv.text = "010-1234-5678"
		binding.organizationTv.text = "(주)Komapper"
		binding.projectList.text = "-"
		return binding.root
	}
	
}