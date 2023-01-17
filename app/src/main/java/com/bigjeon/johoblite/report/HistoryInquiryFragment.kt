package com.bigjeon.johoblite.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bigjeon.johoblite.databinding.FragmentHistoryInquiryBinding
import com.bigjeon.johoblite.viewmodel.MainViewModel

class HistoryInquiryFragment : Fragment() {
	
	private lateinit var binding: FragmentHistoryInquiryBinding
	
	private val model: MainViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentHistoryInquiryBinding.inflate(inflater, container, false)
		
		return binding.root
	}
	
}