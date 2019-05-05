package com.psucoders.shuttler.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.LogoutActivityTemp
import com.psucoders.shuttler.ui.authentication.AuthenticationActivity
import com.psucoders.shuttler.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.login_activity.*

class RegisterActivity : AppCompatActivity() {

    private val registerViewModel = RegisterViewModel()

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
        v.isEnabled = false
        registerViewModel.valid.observe(this, Observer { valid ->
            if (valid != null && !valid) {
                Snackbar.make(registerRoot, "Username and password don't meet requirements.", Snackbar.LENGTH_LONG).show()
                registerViewModel.resetValidity()
                v.isEnabled = true
            }
        })

        registerViewModel.registrationSuccess.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AuthenticationActivity::class.java))
            }
        })
        registerViewModel.handleRegister(edtUserSignUp.text.toString(), edtPasswordSignUp.text.toString())
    }
}
