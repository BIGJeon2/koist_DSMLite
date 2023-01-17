package com.bigjeon.johoblite.analysis

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.webkit.WebViewAssetLoader
import com.bigjeon.johoblite.data.*
import com.bigjeon.johoblite.databinding.FragmentCadBinding
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.listener.OnTouchListener
import com.bigjeon.johoblite.rcvadapter.DrawingToolRcvAdapter
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.viewmodel.MainViewModel
import com.bumptech.glide.Glide


class CadFragment : Fragment(), OnTouchListener  {
	
	companion object {
		var downX:Float = 0.0F
		var downY:Float = 0.0F
		var downXY:Float = 0.0F
	}
	
	private lateinit var binding: FragmentCadBinding
	private val model by activityViewModels<MainViewModel>()
	private lateinit var drawingToolAdapter: DrawingToolRcvAdapter
	private var editable = false
	
	//retrofit2
	private lateinit var serviceClient: OcrFactory
	
	@SuppressLint("SetTextI18n", "UseCompatLoadingForColorStateLists")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentCadBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	@SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val activity = requireActivity()
		val context = requireContext()
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		
		val assetLoader = WebViewAssetLoader.Builder()
			.addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
			.build()
		// Override WebView client, and if request is to local file, intercept and serve local
		binding.wvMain.webChromeClient = WebChromeClient()
		binding.wvMain.webViewClient = WebViewClient()
		// 하드웨어 가속을 통해서 성능을 올린다. 기기에 따라서 차이는 있겠지만 평균적으로 4.4 이상이라면 가속이 유리하다.
		binding.wvMain.setLayerType(View.LAYER_TYPE_HARDWARE, null)
		
		val settings = binding.wvMain.settings
		settings.javaScriptEnabled = true
		settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
		settings.domStorageEnabled = true
		settings.allowUniversalAccessFromFileURLs = true
		settings.allowFileAccess = true
		
		binding.wvMain.setOnTouchListener(this)
		
		model.planeSectionState.observe(viewLifecycleOwner){ source ->
			if (source.code != "NONE"){
				setOBJ()
			}
		}
		
	}
	
	private fun setOBJ(){
		if (model.directionState.value?.Name == "인천계통"){
			binding.wvMain.loadUrl("file:///android_asset/paldang_obj_viewer.html")
		}else if (model.directionState.value?.Name == "평택계통"){
			binding.wvMain.loadUrl("file:///android_asset/seongnam_obj_viewer.html")
		}
	}
	
	private fun getDisplaySize(): Point {
		val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
		val display = windowManager.defaultDisplay
		val size = Point()
		display.getSize(size)
		return size
	}
	
	override fun onDetach() {
		super.onDetach()
		Glide.get(requireActivity()).clearMemory()
	}
}