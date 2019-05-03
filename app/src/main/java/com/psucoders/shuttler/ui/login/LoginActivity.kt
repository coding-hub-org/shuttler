package com.psucoders.shuttler.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.psucoders.shuttler.R
import com.psucoders.shuttler.data.firebase.FirebaseSingleton
import com.psucoders.shuttler.ui.LogoutActivityTemp
import com.psucoders.shuttler.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {

    private val loginViewModel = LoginViewModel()
    private val firebaseObj = FirebaseSingleton.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeExistingUser()
        setContentView(R.layout.login_activity)
    }

    private fun observeExistingUser() {
        loginViewModel.userLoggedIn.observe(this, Observer { userExists ->
            if (userExists) {
                startActivity(Intent(this, LogoutActivityTemp::class.java))
            }
        })
        loginViewModel.checkIfUserExists()
    }

    fun registerUser(v: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun handleLogin(v: View) {
        loginViewModel.loginUser(edtUser.text.toString(), edtPassword.text.toString())
        v.isClickable = false
    }
}