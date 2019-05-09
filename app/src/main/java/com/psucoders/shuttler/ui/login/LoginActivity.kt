package com.psucoders.shuttler.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.authentication.AuthenticationActivity
import com.psucoders.shuttler.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.login_activity.*
import androidx.lifecycle.ViewModelProviders
import com.psucoders.shuttler.DriverActivity
import java.sql.Driver


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        observeExistingUser()
    }

    private fun observeExistingUser() {
        loginViewModel.userLoggedIn.observe(this, Observer { userExists ->
            if (userExists) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }
        })
        loginViewModel.checkIfUserExists()
    }

    fun registerUser(v: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun handleLogin(v: View) {
        btnSignIn.isEnabled = false
        if (!isDriver(edtUser.text.toString(), edtPassword.text.toString())) {
            loginViewModel.loginUser(edtUser.text.toString(), edtPassword.text.toString())
            loginViewModel.validFields.observe(this, Observer { valid ->
                if (valid != null && !valid) {
                    Snackbar.make(loginRoot, "Invalid credentials. Please check your username / password", Snackbar.LENGTH_LONG).show()
                    loginViewModel.resetValidity()
                }
                if (valid != null && valid) {
                    loginViewModel.checkIfUserExists()
                }
                btnSignIn.isEnabled = true
            })
        } else {
            startActivity(Intent(this, DriverActivity::class.java))
        }
    }

    private fun isDriver(email: String, password: String): Boolean {
        if (email == "driver@gmail.com" && password == "driver") {
            return true
        }
        return false
    }
}