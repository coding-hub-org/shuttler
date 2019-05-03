package com.psucoders.shuttler.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun returnToLogin(v: View) {
        val intentToLogin = Intent(this, LoginActivity::class.java)
        startActivity(intentToLogin)
        finish()
    }

    fun handleRegister(v: View) {

    }
}
