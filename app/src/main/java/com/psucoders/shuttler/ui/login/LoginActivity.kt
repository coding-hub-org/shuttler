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
import androidx.lifecycle.ViewModelProvider
import com.psucoders.shuttler.ui.driver.DriverActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        loginViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(LoginViewModel::class.java)
        observeExistingUser()
    }

    private fun observeExistingUser() {
        loginViewModel.userLoggedIn.observe(this, Observer { userExists ->
            if (userExists) {
                loginViewModel.checkIfUserIsDriver()
                loginViewModel.isDriver.observe(this, Observer { isDriver ->
                    if (isDriver) {
                        startActivity(Intent(this, DriverActivity::class.java))
                    } else
                        startActivity(Intent(this, AuthenticationActivity::class.java))
                })
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
    }
}