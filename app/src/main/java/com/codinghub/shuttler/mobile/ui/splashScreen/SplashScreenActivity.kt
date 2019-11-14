package com.codinghub.shuttler.mobile.ui.splashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codinghub.shuttler.mobile.R
import com.codinghub.shuttler.mobile.ui.login.LoginActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
