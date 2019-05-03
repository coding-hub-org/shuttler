package com.psucoders.shuttler.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.psucoders.shuttler.R
import com.psucoders.shuttler.data.firebase.FirebaseSingleton
import com.psucoders.shuttler.ui.login.LoginActivity

class LogoutActivityTemp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout_temp)
    }

    fun logout(v: View) {
        val firebase = FirebaseSingleton.getInstance()
        firebase.logout()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
