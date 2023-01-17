package com.bigjeon.johoblite.userpage

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bigjeon.johoblite.service.OcrFactory
import com.bigjeon.johoblite.data.ChangePw
import com.bigjeon.johoblite.databinding.FragmentPasswordBinding
import com.bigjeon.johoblite.library.ApiClient
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class PasswordFragment : Fragment() {
	
	companion object val digits = "0123456789ABCDEF"
	
	private lateinit var binding: FragmentPasswordBinding
	private lateinit var sharedPreference: SharedPreferences
	
	//retrofit2
	private lateinit var serviceClient: OcrFactory
	
	@SuppressLint("CommitPrefEdits")
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentPasswordBinding.inflate(inflater, container, false)
		sharedPreference =  requireContext().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
		serviceClient = ApiClient.retrofit.create(OcrFactory::class.java)
		var editor = sharedPreference.edit()
		
		binding.confirmBtn.setOnClickListener {
			if (binding.oldPasswordET.text?.isNotEmpty() == true && binding.newPasswordET.text?.isNotEmpty() == true && binding.confirmPasswordET.text?.isNotEmpty() == true){
				changeUserPWDB()
			}
		}
		
		return binding.root
	}
	
	private fun changeUserPWDB(){
		val confirmDialogBuilder = AlertDialog.Builder(requireActivity())
		confirmDialogBuilder.setTitle("비밀번호 변경")
		confirmDialogBuilder.setMessage("정말 비밀번호를 변경하시겠습니까?\n(이전 비밀번호는 저장되지 않습니다.)")
		confirmDialogBuilder.setPositiveButton("변경하기"
		) { _, _ ->
			CoroutineScope(Dispatchers.IO).launch {
				serviceClient.setUserPassword(ChangePw(serialization(binding.oldPasswordET.text.toString()), serialization(binding.newPasswordET.text.toString()), serialization(binding.confirmPasswordET.text.toString()))).enqueue(object : retrofit2.Callback<JsonObject> {
					override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
						if (response.body()?.getAsJsonObject("changeResult")?.get("result")?.asString.equals("true")){
							binding.oldPasswordET.text?.clear()
							binding.newPasswordET.text?.clear()
							binding.confirmPasswordET.text?.clear()
							Toast.makeText(requireContext(), "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show()
						}else{
							Toast.makeText(requireContext(), response.body()?.getAsJsonObject("changeResult")?.get("msg")?.asString, Toast.LENGTH_SHORT).show()
						}
					}
					override fun onFailure(call: Call<JsonObject>, t: Throwable) {
					}
				})
			}
		}
		confirmDialogBuilder.setNegativeButton("취소"
		) { _, _ ->
			binding.oldPasswordET.text?.clear()
			binding.newPasswordET.text?.clear()
			binding.confirmPasswordET.text?.clear()
		}
		confirmDialogBuilder.create().show()
		/*var inflater = requireActivity().layoutInflater
		confirmDialogBuilder.setView(inflater.inflate(R.layout.confirm_dialog))*/
		
	}
	
	private fun serialization(text: String) : String{
		val iv = ByteArray(16)
		SecureRandom().nextBytes(iv)
		val key1 = "this_is_komapper_encryption_key!".toByteArray()
		val secretKeySpec = SecretKeySpec(key1, "AES")
		val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IvParameterSpec(iv))
		var encrypted = cipher.update(text.toByteArray())
		encrypted = concat(encrypted, cipher.doFinal())
		return byteArrayTooHex(iv)+":"+byteArrayTooHex(encrypted)
	}
	
	private fun byteArrayTooHex(array: ByteArray): String{
		val hexChars = CharArray(array.size * 2)
		for (i in array.indices) {
			val v = array[i].toInt() and 0xff
			hexChars[i * 2] = digits[v shr 4]
			hexChars[i * 2 + 1] = digits[v and 0xf]
		}
		return String(hexChars)
	}
	fun concat(vararg arrays: ByteArray): ByteArray {
		val out = ByteArrayOutputStream()
		arrays.forEach { out.write(it) }
		return out.toByteArray()
	}
	
}