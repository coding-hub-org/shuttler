package com.psucoders.shuttler.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private val loginViewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeExistingUser()
        setContentView(R.layout.login_activity)
    }

    private fun observeExistingUser() {
        loginViewModel.userLoggedIn.observe(this, Observer { userExists ->
            if (userExists) {
                // Redirect to map page or driver page
            }
        })
        loginViewModel.checkIfUserExists()
    }

    fun registerUser(v: View) {
        val intentToRegister = Intent(this, RegisterActivity::class.java)
        startActivity(intentToRegister)
    }
}