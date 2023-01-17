package com.bigjeon.johoblite.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.databinding.ConfirmDialogBinding
import com.bigjeon.johoblite.inter.ConfirmInterface

class ConfirmDialog(x: Int, y: Int, dialogConfirmInterface: ConfirmInterface) : DialogFragment() {
	// 뷰 바인딩 정의
	private var _binding: ConfirmDialogBinding? = null
	private val binding get() = _binding!!
	
	private var confirmDialogInterface: ConfirmInterface? = null
	
	private var deviceX: Int = 0
	private var deviceY: Int = 0
	
	init {
		this.deviceX = x
		this.deviceY = y
		this.confirmDialogInterface = dialogConfirmInterface
	}
	
	@SuppressLint("ClickableViewAccessibility")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_binding = ConfirmDialogBinding.inflate(inflater, container, false)
		val view = binding.root
		
		binding.dialogScriptTV.text = "현재 저장하지 않은 목록이 있습니다. \n 저장 후 편집모드를 종료하시겠습니까?"
		binding.dialogSubScriptTV.text = "( 미 저장시 신규로 작성한 목록은 다시 불러 오실 수 없습니다. )"
		context?.let { binding.dialogSubScriptTV.setTextColor(it.getColor(R.color.red)) }
		
		binding.confirmBtn.text = requireContext().getString(R.string.save)
		
		// 취소 버튼 클릭
		binding.confirmBtn.setOnClickListener {
			this.confirmDialogInterface?.onSaveButtonClick()
			dismiss()
		}
		// 확인 버튼 클릭
		binding.closeBtn.setOnClickListener {
			this.confirmDialogInterface?.onCloseButtonClick()
			dismiss()
		}
		return view
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
	
	override fun onResume() {
		super.onResume()
		val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
		params?.width = (deviceX * 0.5).toInt()
		params?.height = (deviceY * 0.4).toInt()
		dialog?.window?.attributes = params as WindowManager.LayoutParams
	}
}