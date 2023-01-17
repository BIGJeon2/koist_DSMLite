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
import com.bigjeon.johoblite.databinding.FragmentReportSubTitleBinding
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.google.android.material.color.MaterialColors

class ReportSubTitleFragment : Fragment() {
	
	private lateinit var binding: FragmentReportSubTitleBinding
	private val model: MainViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentReportSubTitleBinding.inflate(inflater, container, false)
		model.subTitleState.observe(viewLifecycleOwner) { source ->
			
			binding.subTitleDigitalReportBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleDigitalReportTv.setTextColor(requireActivity().resources.getColor(R.color.disabled_text_color, null))
			binding.subTitleCadReportBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleCadReportTv.setTextColor(requireActivity().resources.getColor(R.color.secondary_text_color, null))
			binding.subTitleHistoryBtn.setBackgroundColor(requireActivity().resources.getColor(R.color.TRANSPARENT, null))
			binding.subTitleHistoryTv.setTextColor(requireActivity().resources.getColor(R.color.disabled_text_color, null))
			
			when(source){
				resources.getString(R.string.Digital_Report) -> {
					binding.subTitleDigitalReportBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleDigitalReportTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.Original_Report) -> {
					binding.subTitleCadReportBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleCadReportTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
				resources.getString(R.string.History_Report) -> {
					binding.subTitleHistoryBtn.setBackgroundColor(MaterialColors.getColor(requireContext(), R.attr.secondary_selected_color, Color.BLACK))
					binding.subTitleHistoryTv.setTextColor(MaterialColors.getColor(requireContext(), R.attr.default_color, Color.BLACK))
				}
			}
		}
		
		binding.subTitleDigitalReportBtn.setOnClickListener { /*changeSubTitleState(binding.subTitleDigitalReportTv.text as String)*/Toast.makeText(requireActivity(), "개발중인 기능입니다.", Toast.LENGTH_SHORT).show() }
		binding.subTitleCadReportBtn.setOnClickListener {  changeSubTitleState(binding.subTitleCadReportTv.text as String) }
		binding.subTitleHistoryBtn.setOnClickListener { Toast.makeText(requireActivity(), "개발중인 기능입니다.", Toast.LENGTH_SHORT).show() }
		
		return binding.root
	}
	
	private fun changeSubTitleState(subTitle: String){
		if (model.subTitleState.value != subTitle){
			model.setSubTitle(subTitle)
		}
	}
	
}