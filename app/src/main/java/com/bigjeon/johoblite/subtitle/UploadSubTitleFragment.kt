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
import com.bigjeon.johoblite.databinding.FragmentUploadSubTitleBinding
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.google.android.material.color.MaterialColors


class UploadSubTitleFragment : Fragment() {
	
	private lateinit var binding: FragmentUploadSubTitleBinding
	private val model: MainViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentUploadSubTitleBinding.inflate(inflater, container, false)
		model.subTitleState.observe(viewLifecycleOwner) { source ->
			
			binding.subTitleGuideBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleGuideTv.setTextColor(requireActivity().resources.getColor(R.color.disabled_text_color, null))
			binding.subTitleUploadBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleUploadTv.setTextColor(requireActivity().resources.getColor(R.color.disabled_text_color, null))
			binding.subTitleScaleBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleScaleTv.setTextColor(requireActivity().resources.getColor(R.color.disabled_text_color, null))
			binding.subTitleInspectionBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleInspectionTv.setTextColor(requireActivity().resources.getColor(R.color.secondary_text_color, null))
			
			when(source){
				resources.getString(R.string.Guide_Upload) -> {
					binding.subTitleGuideBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleGuideTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.Upload_Upload) -> {
					binding.subTitleUploadBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleUploadTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.Scale_Upload) -> {
					binding.subTitleScaleBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleScaleTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.Inspection_Upload) -> {
					binding.subTitleInspectionBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleInspectionTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
			}
		}
		
		binding.subTitleGuideBtn.setOnClickListener { Toast.makeText(requireActivity(), "개발중인 기능입니다.", Toast.LENGTH_SHORT).show()}
		binding.subTitleUploadBtn.setOnClickListener { Toast.makeText(requireActivity(), "개발중인 기능입니다.", Toast.LENGTH_SHORT).show() }
		binding.subTitleScaleBtn.setOnClickListener { Toast.makeText(requireActivity(), "개발중인 기능입니다.", Toast.LENGTH_SHORT).show() }
		binding.subTitleInspectionBtn.setOnClickListener { changeSubTitleState(binding.subTitleInspectionTv.text as String) }
		
		return binding.root
	}
	
	private fun changeSubTitleState(subTitle: String){
		if (model.subTitleState.value != subTitle){
			model.setSubTitle(subTitle)
		}
	}
}