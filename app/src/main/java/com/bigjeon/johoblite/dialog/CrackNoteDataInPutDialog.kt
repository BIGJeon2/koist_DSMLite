package com.bigjeon.johoblite.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.bigjeon.johoblite.R
import com.bigjeon.johoblite.data.Crack
import com.bigjeon.johoblite.databinding.DialogCrackNoteBinding
import com.bigjeon.johoblite.inter.ConfirmDialogInterface
import kotlin.math.roundToInt

class CrackNoteDataInPut(x: Int, y: Int, dialogConfirmDialogInterface: ConfirmDialogInterface, crack: Crack, id: Int) : DialogFragment() {
	
	// 뷰 바인딩 정의
	private var _binding: DialogCrackNoteBinding? = null
	private val binding get() = _binding!!
	
	private var confirmDialogInterface: ConfirmDialogInterface? = null
	
	private var repairStateList = arrayListOf<String>("미완료", "보수 완료")
	
	private var deviceX: Int = 0
	private var deviceY: Int = 0
	private var item: Crack? = null
	private var id: Int? = null
	
	init {
		this.deviceX = x
		this.deviceY = y
		this.item = crack
		this.id = id
		this.confirmDialogInterface = dialogConfirmDialogInterface
	}
	
	@SuppressLint("SetTextI18n")
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = DialogCrackNoteBinding.inflate(inflater, container, false)
		
		val view = binding.root
		
		binding.crackNumberTv.text = id.toString()
		when(item?.crackType){
			"CRK001" -> {
				binding.crackTypeTv.text = "균열"
			}
			"CRK002" -> {
				binding.crackTypeTv.text = "망상 균열"
			}
			"CRK003" -> {
				binding.crackTypeTv.text = "박리, 들뜸"
			}
			"CRK004" -> {
				binding.crackTypeTv.text = "누수, 습운부"
			}
			"CRK005" -> {
				binding.crackTypeTv.text = "표면 부식"
			}
			"CRK006" -> {
				binding.crackTypeTv.text = "박락, 파손"
			}
			"CRK007" -> {
				binding.crackTypeTv.text = "철근 노출"
			}
			"CRK008" -> {
				binding.crackTypeTv.text = "재료 분리"
			}
			"CRK009" -> {
				binding.crackTypeTv.text = "백태"
			}
		}
		binding.crackAreaTv.text = item!!.crackWidth.toString() + "*" + item!!.crackLength.toString() + " = " + (item!!.crackWidth * item!!.crackLength * 100).roundToInt() / 100.0 + "㎡"
		binding.crackInputTv.text = "A.I Detection"
		if (item!!.note == "NONE"){
			binding.noteEditTextView.hint = "기입된 내용이 없습니다."
		}else{
			binding.noteEditTextView.setText(item!!.note)
		}
		val repairAdapter = context?.let { ArrayAdapter(it, R.layout.spinnertv, repairStateList) }
		repairAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		binding.repairStateSpinner.adapter = repairAdapter
		binding.repairStateSpinner.dropDownVerticalOffset = dipToPixels(40f).toInt()
		binding.repairStateSpinner.onItemSelectedListener =
			object : AdapterView.OnItemSelectedListener {
				override fun onItemSelected(
					adapterView: AdapterView<*>?,
					view: View?,
					position: Int,
					id: Long
				) {
					when(position){
						0 -> {
							item!!.repairState = false
						}
						1 -> {
							item!!.repairState = true
						}
					}
				}
				override fun onNothingSelected(p0: AdapterView<*>?) {
				}
			}
		if (item!!.repairState){
			binding.repairStateSpinner.setSelection(1)
		}else{
			binding.repairStateSpinner.setSelection(0)
		}
		// 취소 버튼 클릭
		binding.closeBtn.setOnClickListener {
			dismiss()
		}
		
		// 확인 버튼 클릭
		binding.saveBtn.setOnClickListener {
			item!!.note = binding.noteEditTextView.text.toString()
			Log.d("DIALOG", item!!.note.toString())
			this.confirmDialogInterface?.onSaveButtonClick(item!!, id)
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
		params?.width = (deviceX * 0.8).toInt()
		params?.height = (deviceY * 0.8).toInt()
		dialog?.window?.attributes = params as WindowManager.LayoutParams
	}
	private fun dipToPixels(dipValue: Float): Float {
		return TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			dipValue,
			resources.displayMetrics
		)
	}
}
