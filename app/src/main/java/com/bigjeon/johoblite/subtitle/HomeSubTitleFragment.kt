package com.bigjeon.johoblite.subtitle

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.databinding.FragmentHomeSubTitleBinding
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.google.android.material.color.MaterialColors

class HomeSubTitleFragment : Fragment() {
	
	private lateinit var binding: FragmentHomeSubTitleBinding
	private val model: MainViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentHomeSubTitleBinding.inflate(inflater, container, false)
		
		val activity = requireActivity()
		val context = requireContext()
		
		model.subTitleState.observe(viewLifecycleOwner) { source ->
			binding.subTitleFacilityBtn.setBackgroundColor(activity.resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleFacilityTv.setTextColor(activity.resources.getColor(R.color.secondary_text_color, null))
			binding.subTitleProgressBtn.setBackgroundColor(activity.resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleProgressTv.setTextColor(activity.resources.getColor(R.color.disabled_text_color, null))
			binding.subTitleInfoBtn.setBackgroundColor(activity.resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleInfoTv.setTextColor(activity.resources.getColor(R.color.disabled_text_color, null))
			
			when(source){
				resources.getString(R.string.Facility_Home) -> {
					binding.subTitleFacilityBtn.setBackgroundColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleFacilityTv.setTextColor(MaterialColors.getColor(context, R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.Progress_Home) -> {
					binding.subTitleProgressBtn.setBackgroundColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleProgressTv.setTextColor(MaterialColors.getColor(context, R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.Info_Home) -> {
					binding.subTitleInfoBtn.setBackgroundColor(MaterialColors.getColor(context, R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleInfoTv.setTextColor(MaterialColors.getColor(context, R.attr.default_color, Color.BLACK))
				}
			}
		}
		
		binding.subTitleFacilityBtn.setOnClickListener { changeSubTitleState(binding.subTitleFacilityTv.text as String)}
		binding.subTitleProgressBtn.setOnClickListener { Toast.makeText(activity, "개발중인 기능입니다.", Toast.LENGTH_SHORT).show() }
		binding.subTitleInfoBtn.setOnClickListener { Toast.makeText(activity, "개발중인 기능입니다.", Toast.LENGTH_SHORT).show() }
		
		return binding.root
	}
	
	private fun changeSubTitleState(subTitle: String){
		if (model.subTitleState.value != subTitle){
			model.setSubTitle(subTitle)
		}
	}
}