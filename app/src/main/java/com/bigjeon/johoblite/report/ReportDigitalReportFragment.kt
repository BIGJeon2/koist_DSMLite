package com.bigjeon.johoblite.report

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.databinding.FragmentReportDigitalReportBinding
import com.bigjeon.johoblite.library.ApiClient

class ReportDigitalReportFragment : Fragment() {
	
	private lateinit var binding: FragmentReportDigitalReportBinding
	
	//retrofit2
	lateinit var serviceClient: OcrFactory
	
	@SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentReportDigitalReportBinding.inflate(inflater, container, false)
		
		val activity = requireActivity()
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		
		binding.webView.settings.apply {
			javaScriptEnabled = true
			useWideViewPort = true
			binding.webView.webViewClient = WebViewClient()
			setSupportZoom(true)
			domStorageEnabled = true
			allowContentAccess = true
			allowFileAccess = true
		}
		
		binding.webView.loadUrl(requireContext().getString(R.string.URL) + "data/template/report.html")
		
		
		binding.scriptBtn.setOnClickListener {
			if (binding.scriptBtn.text.equals("Print Report")){
				binding.scriptBtn.text = "Back"
				binding.webView.loadUrl("https://datain.co.kr/komapper/option.html")
			}else{
				if (binding.webView.canGoBack()){
					binding.webView.goBack()
				}else{
					binding.scriptBtn.text = "Print Report"
				}
			}
		}
		
		return binding.root
	}
	
}