package com.codinghub.shuttler.mobile.ui.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.codinghub.shuttler.mobile.utils.notifications.MyFirebaseMessagingService
import com.codinghub.shuttler.mobile.R
import com.codinghub.shuttler.mobile.ui.authentication.AuthenticationActivity
import com.codinghub.shuttler.mobile.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
    }

    fun returnToLogin(v: View) {
        val intentToLogin = Intent(this, LoginActivity::class.java)
        startActivity(intentToLogin)
        finish()
    }

    fun handleRegister(v: View) {

        registerViewModel.getFcmToken.observe(this, Observer { token ->
            val fms = MyFirebaseMessagingService()
            fms.setNewToken(token, this)
        })

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
                setDefaultSettingsToSharedPreferences()
                startActivity(Intent(this, AuthenticationActivity::class.java))
            }
        })
        registerViewModel.handleRegister(edtUserSignUp.text.toString(), edtPasswordSignUp.text.toString())
    }

    private fun setDefaultSettingsToSharedPreferences() {
        val sharedPref = getSharedPreferences("_", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("notifyLocation", "Walmart")
        editor.putString("timeAhead", "5")
        editor.putBoolean("notificationsEnabled", true)
        editor.apply()
    }
}
