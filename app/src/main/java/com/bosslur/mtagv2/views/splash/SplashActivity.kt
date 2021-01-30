package com.bosslur.mtagv2.views.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bosslur.mtagv2.R
import com.bosslur.mtagv2.hawkstorage.HawkStorage
import com.bosslur.mtagv2.views.login.LoginActivity
import com.bosslur.mtagv2.views.main.MainActivity
import org.jetbrains.anko.startActivity

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        init()
    }

    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({
            checkIsLogin()
        },1200)
    }

    private fun checkIsLogin() {
        val isLogin = HawkStorage.instance(this).isLogin()
        if(isLogin){
            startActivity<MainActivity>()
            finishAffinity()
        }
        else{
            startActivity<LoginActivity>()
            finishAffinity()
        }
    }
}