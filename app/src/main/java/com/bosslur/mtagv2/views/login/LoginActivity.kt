package com.bosslur.mtagv2.views.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bosslur.mtagv2.R
import com.bosslur.mtagv2.databinding.ActivityLoginBinding
import com.bosslur.mtagv2.views.forgotpassword.ForgotPasswordActivity
import com.bosslur.mtagv2.views.main.MainActivity
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClick()
    }

    private fun onClick() {
       binding.btnLogin.setOnClickListener {
           startActivity<MainActivity>()
       }

        binding.btnForgotPassword.setOnClickListener {
            startActivity<ForgotPasswordActivity>()
        }
    }
}