package com.bosslur.mtagv2.views.forgotpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import com.bosslur.mtagv2.databinding.ActivityForgotPasswordBinding
import com.bosslur.mtagv2.dialog.MyDialog
import com.bosslur.mtagv2.model.ForgotPasswordResponse
import com.bosslur.mtagv2.networking.ApiServices
import com.bosslur.mtagv2.networking.RetrofitClient
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

@Suppress("DEPRECATION")
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        onClick()
    }

    private fun onClick() {
        binding.tbForgotPassword.setNavigationOnClickListener {
            finish()
        }

        binding.btnForgotPasswordPage.setOnClickListener {
            val email = binding.etEmailForgotPassword.text.toString()
            if (isFormValid(email)){
                forgotPasswordToServer(email)
            }
        }
    }

    private fun forgotPasswordToServer(email: String) {
        val forgotPasswordRequest = ForgotPasswordRequest(email= email)
        val forgotPasswordRequestString = Gson().toJson(forgotPasswordRequest)

        MyDialog.showProgressDialog(this)

        ApiServices.getMtagServices().forgotPasswordRequest(forgotPasswordRequestString)
                .enqueue(object : Callback<ForgotPasswordResponse>{
                    override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                        MyDialog.hideDialog()
                        if (response.isSuccessful){
                            val message = response.body()?.message
                            MyDialog.dynamicDialog(
                                    this@ForgotPasswordActivity,
                                    "Success",
                                    "Email Reset Link Sudah dikirim"
                            )
                            Handler(Looper.getMainLooper()).postDelayed({
                                                  MyDialog.hideDialog()
                                finish()
                            },2000)
                        }else{
                            val errorConverter: Converter<ResponseBody, ForgotPasswordResponse> =
                                    RetrofitClient.getClient()
                                            .responseBodyConverter(
                                                    ForgotPasswordResponse::class.java,
                                                    arrayOfNulls<Annotation>(0)
                                            )
                            var errorResponse: ForgotPasswordResponse?
                            try {
                                response.errorBody()?.let {
                                    errorResponse = errorConverter.convert(it)
                                    MyDialog.dynamicDialog(
                                            this@ForgotPasswordActivity,
                                            "Email tidak terkirim",
                                            errorResponse?.message.toString()
                                    )
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                                MyDialog.dynamicDialog(
                                        this@ForgotPasswordActivity,
                                        "Email tidak terkirim",
                                        e.message.toString()
                                )
                                Log.e(TAG, "Error: ${e.message}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                        MyDialog.hideDialog()
                        Log.e(TAG,"Error: ${t.message}")
                    }
                })


    }

    private fun isFormValid(email: String): Boolean {
        if (email.isEmpty()){
            binding.etEmailForgotPassword.error = "Isi Emailnya kawan"
            binding.etEmailForgotPassword.requestFocus()
        }
        else if(! Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmailForgotPassword.error = "Format Email salah kawan"
            binding.etEmailForgotPassword.requestFocus()
        }
        else
        {
            binding.etEmailForgotPassword.error = null
            return true
        }

        return false
    }

    private  fun init(){
        setSupportActionBar(binding.tbForgotPassword)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        private val TAG = ForgotPasswordActivity::class.java.simpleName
    }
}