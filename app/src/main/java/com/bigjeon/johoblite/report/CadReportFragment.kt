package com.bigjeon.johoblite.report

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bigjeon.johoblite.databinding.FragmentCadReportBinding
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.viewmodel.MainViewModel
import java.io.*


class CadReportFragment : Fragment() {

	private lateinit var binding: FragmentCadReportBinding
	private val model by activityViewModels<MainViewModel>()
	
	//retrofit2
	lateinit var serviceClient: OcrFactory
	
	@SuppressLint("SetJavaScriptEnabled")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentCadReportBinding.inflate(inflater, container, false)
		
		return binding.root
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		
		model.planeSectionState.observe(requireActivity()){ source ->
			if (source.code != "NONE"){
				Log.d("CADREPORT","REFRESHED")
				binding.loadingImageview.visibility = View.VISIBLE
				loadPDF()
			}
		}
		
		binding.webView.webChromeClient = object : WebChromeClient() {
			override fun onProgressChanged(view: WebView, newProgress: Int) {
				Log.d("로딩", newProgress.toString())
				if (newProgress == 100){
					binding.loadingImageview.visibility = View.GONE
				}
				super.onProgressChanged(view, newProgress)
			}
		}
		
		binding.webView.settings.apply {
			javaScriptEnabled = true
			useWideViewPort = true
			loadWithOverviewMode = true
			builtInZoomControls = true
			setSupportZoom(true)
			domStorageEnabled = true
			allowContentAccess = true
			allowFileAccess = true
		}
	}
	
	private fun loadPDF(){
		if (model.directionState.value?.Name == "인천계통"){
			binding.webView.loadUrl("https://docs.google.com/gview?embedded=true&url=http://101.101.216.123:5001/demo/paldang_pdf.pdf")
		}else if (model.directionState.value?.Name == "평택계통"){
			binding.webView.loadUrl("https://docs.google.com/gview?embedded=true&url=http://101.101.216.123:5001/demo/seongnam_pdf.pdf")
		}
	}
	
}