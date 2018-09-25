package com.psucoders.shuttler

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login(btnSignIn)
    }

    fun login(button: Button) {
        button.setOnClickListener {
            val intent = Intent(this@LoginActivity, TrackerActivity::class.java)
            startActivity(intent)
        }
    }
}
