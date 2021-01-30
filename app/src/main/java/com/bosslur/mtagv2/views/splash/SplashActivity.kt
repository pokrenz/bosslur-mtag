package com.bosslur.mtagv2.views.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.bosslur.mtagv2.R
import com.bosslur.mtagv2.views.main.MainActivity

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        init()
    }

    private fun init() {
        Handler().postDelayed({
            MainActivity::class.java
        },1200)
    }
}