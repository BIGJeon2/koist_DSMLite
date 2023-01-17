package com.bigjeon.johoblite.dialog

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.MediaController
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.databinding.TutorialDialogBinding


class TutorialDialog(x: Int, y: Int) : DialogFragment() {
	// 뷰 바인딩 정의
	private var _binding: TutorialDialogBinding? = null
	private val binding get() = _binding!!
	
	private var deviceX: Int = 0
	private var deviceY: Int = 0
	
	init {
		this.deviceX = x
		this.deviceY = y
	}
	
	@SuppressLint("ClickableViewAccessibility")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_binding = TutorialDialogBinding.inflate(inflater, container, false)
		val view = binding.root
		val mc = MediaController(context)
		
		val video : Uri = Uri.parse(requireContext().getString(R.string.URL) + "data/tutorial/tutorial.mp4")
		
		binding.videoView.setMediaController(mc)
		binding.videoView.setVideoURI(video)
		binding.videoView.start()
		
		val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		lp.gravity = Gravity.BOTTOM
		mc.layoutParams = lp
		(mc.parent as ViewGroup).removeView(mc)
		binding.frame.addView(mc)
		
		binding.videoView.setOnClickListener {
			Log.d("videoview", "onClick")
			if (mc.isVisible){
				Log.d("videoview", "onClick1")
				mc.visibility = View.GONE
			}else{
				Log.d("videoview", "onClick2")
				mc.visibility = View.VISIBLE
			}
		}
		
		// 취소 버튼 클릭
		binding.closeBtn.setOnClickListener {
			dismiss()
		}
		return view
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		binding.videoView.stopPlayback()
		_binding = null
	}
	
	override fun onResume() {
		super.onResume()
		val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
		params?.width = (deviceX * 0.712).toInt()
		params?.height = (deviceY * 0.8).toInt()
		dialog?.window?.attributes = params as WindowManager.LayoutParams
	}
	
}