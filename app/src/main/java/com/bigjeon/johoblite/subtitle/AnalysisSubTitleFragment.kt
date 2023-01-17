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
import com.bigjeon.johoblite.databinding.FragmentAnalysisSubTitleBinding
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.google.android.material.color.MaterialColors

class AnalysisSubTitleFragment : Fragment() {
	
	private lateinit var binding: FragmentAnalysisSubTitleBinding
	private val model: MainViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentAnalysisSubTitleBinding.inflate(inflater, container, false)
		
		model.subTitleState.observe(viewLifecycleOwner) { source ->
			
			binding.subTitleStitchingBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleStitchingTv.setTextColor(requireActivity().resources.getColor(R.color.disabled_text_color, null))
			binding.subTitleStitchingInspectionBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleStitchingInspectionTv.setTextColor(requireActivity().resources.getColor(R.color.secondary_text_color, null))
			binding.subTitleDetectionBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleDetectionTv.setTextColor(requireActivity().resources.getColor(R.color.disabled_text_color, null))
			binding.subTitleDetectionInspectionBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleDetectionInspectionTv.setTextColor(requireActivity().resources.getColor(R.color.disabled_text_color, null))
			binding.subTitleRemunerationBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleRemunerationTv.setTextColor(requireActivity().resources.getColor(R.color.disabled_text_color, null))
			binding.subTitleCadBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleCadTv.setTextColor(requireActivity().resources.getColor(R.color.secondary_text_color, null))
			
			when(source){
				resources.getString(R.string.Stitching_Analysis) -> {
					binding.subTitleStitchingBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleStitchingTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.Stitching_Inspection_Analysis) -> {
					binding.subTitleStitchingInspectionBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleStitchingInspectionTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.AI_Analysis) -> {
					binding.subTitleDetectionBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleDetectionTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.AI_Inspection_Analysis) -> {
					binding.subTitleDetectionInspectionBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleDetectionInspectionTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.Remuneration_Investigation_Analysis) -> {
					binding.subTitleRemunerationBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleRemunerationTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.cad_Analysis) -> {
					binding.subTitleCadBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleCadTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
			}
		}
		
		binding.subTitleStitchingBtn.setOnClickListener { Toast.makeText(requireActivity(), "개발중인 기능입니다.", Toast.LENGTH_SHORT).show()}
		binding.subTitleStitchingInspectionBtn.setOnClickListener { changeSubTitleState(binding.subTitleStitchingInspectionTv.text as String) }
		binding.subTitleDetectionBtn.setOnClickListener { Toast.makeText(requireActivity(), "개발중인 기능입니다.", Toast.LENGTH_SHORT).show() }
		binding.subTitleDetectionInspectionBtn.setOnClickListener { /*changeSubTitleState(binding.subTitleDetectionInspectionTv.text as String)*/Toast.makeText(requireActivity(), "개발중인 기능입니다.", Toast.LENGTH_SHORT).show()  }
		binding.subTitleRemunerationBtn.setOnClickListener { /*changeSubTitleState(binding.subTitleRemunerationTv.text as String)*/Toast.makeText(requireActivity(), "개발중인 기능입니다.", Toast.LENGTH_SHORT).show()  }
		binding.subTitleCadBtn.setOnClickListener { changeSubTitleState(binding.subTitleCadTv.text as String) }
		return binding.root
	}
	
	private fun changeSubTitleState(subTitle: String){
		if (model.subTitleState.value != subTitle){
			model.setSubTitle(subTitle)
		}
	}
}