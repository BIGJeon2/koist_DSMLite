package com.bigjeon.johoblite

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bigjeon.johoblite.data.User
import com.bigjeon.johoblite.databinding.ActivityLoginBinding
import com.bigjeon.johoblite.library.ApiClient
import com.bigjeon.johoblite.service.OcrFactory
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class LoginActivity : AppCompatActivity() {
    
    private val digits = "0123456789ABCDEF"
    
    lateinit var binding: ActivityLoginBinding
    private lateinit var service: OcrFactory

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Komapperlite_default)
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setContentView(binding.root)
        //koist001
        //A01234
        binding.editTextTextAddress.setText("")
        binding.editTextTextPassword.setText("")
        service = ApiClient.retrofit.create(OcrFactory::class.java)
        binding.login.setOnClickListener {getUser()}
    }

    private fun goMain(user: User, userTheme : String, pass: String){
        if (user.id == "koist001" && pass == "A1234") {
            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString("USER_ID", user.id)
            editor.putString("USER_NAME", user.name)
            editor.putBoolean("USER_ADMIN", user.is_admin)
            when (userTheme) {
                "navy" -> editor.putInt("USER_THEME", 0)
                "orange" -> editor.putInt("USER_THEME", 1)
                "green" -> editor.putInt("USER_THEME", 2)
                "purple" -> editor.putInt("USER_THEME", 3)
                "brown" -> editor.putInt("USER_THEME", 4)
            }
            editor.apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            Toast.makeText(applicationContext, "환영합니다.", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext, "일치하는 계정을 찾지못하였습니다. 확인 후 다시 시도해주십시요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUser() {
        setUserInfo()
        /*CoroutineScope(IO).launch {
            service.postUserCheck(PostUserModel(binding.editTextTextAddress.text.toString(), serialization(binding.editTextTextPassword.text.toString()))).enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.get("result").asString.equals("true")){
                            setUserInfo()
                        }else{
                            Toast.makeText(applicationContext, response.body()!!.get("message").asString, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                }
            })
        }*/
    }

    private fun setUserInfo(){
        var userTheme = "navy"
        goMain(User(binding.editTextTextAddress.text.toString(), "사용자1", false), userTheme,binding.editTextTextPassword.text.toString())
        
        /*CoroutineScope(IO).launch {
            
            service.getUserThemeInInfo().enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        userTheme = response.body()?.getAsJsonObject("themeInfo")?.get("THEME")?.asString.toString()
                    }
                }
        
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    TODO("Not yet implemented")
                }
        
            })
            
            service.getUserSignInInfo().enqueue(object : retrofit2.Callback<PostUserResult> {
                override fun onResponse(call: Call<PostUserResult>, response: Response<PostUserResult>) {
                    if (response.isSuccessful) {
                        Log.d("setUserInfo", "성공${response.body()}")
                        val user: User = response.body()!!.user
                        goMain(user, userTheme)
                    }
                }
                override fun onFailure(call: Call<PostUserResult>, t: Throwable) {
                }
            })
        }*/
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