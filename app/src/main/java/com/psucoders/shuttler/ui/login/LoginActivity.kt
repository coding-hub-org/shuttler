package com.psucoders.shuttler.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.util.Pair
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.authentication.AuthenticationActivity
import com.psucoders.shuttler.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.login_activity.*
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.psucoders.shuttler.EmailActivity
import com.psucoders.shuttler.ui.driver.DriverActivity
import com.psucoders.shuttler.ui.forgotPassword.ForgotPassword


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        emailEditText.setOnClickListener {
            val loginPromptTextView = findViewById<TextView>(R.id.loginPromptTextView)
            val emailEditText = findViewById<EditText>(R.id.emailEditText)
            val prompt = Pair.create(loginPromptTextView as View, "prompt")
            val email = Pair.create(emailEditText as View, "email")

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, prompt, email)
            startActivity(Intent(this, EmailActivity::class.java), options.toBundle())
        }

//        loginViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(LoginViewModel::class.java)
//        observeExistingUser()observeExistingUser
    }

//    private fun observeExistingUser() {
//        loginViewModel.userLoggedIn.observe(this, Observer { userExists ->
//            if (userExists) {
//                loginViewModel.checkIfUserIsDriver()
//                loginViewModel.isDriver.observe(this, Observer { isDriver ->
//                    if (isDriver) {
//                        startActivity(Intent(this, DriverActivity::class.java))
//                    } else
//                        startActivity(Intent(this, AuthenticationActivity::class.java))
//                })
//                finish()
//            }
//        })
//        loginViewModel.checkIfUserExists()
//    }
//
//    fun registerUser(v: View) {
//        startActivity(Intent(this, RegisterActivity::class.java))
//    }
//
//    fun forgotPassword(v: View){
//        startActivity(Intent(this, ForgotPassword::class.java))
//    }
//
//    fun handleLogin(v: View) {
//        btnSignIn.isEnabled = false
//        loginViewModel.loginUser(edtUser.text.toString(), edtPassword.text.toString())
//        loginViewModel.validFields.observe(this, Observer { valid ->
//            if (valid != null && !valid) {
//                Snackbar.make(loginRoot, "Invalid credentials. Please check your username / password", Snackbar.LENGTH_LONG).show()
//                loginViewModel.resetValidity()
//            }
//            if (valid != null && valid) {
//                loginViewModel.checkIfUserExists()
//            }
//            btnSignIn.isEnabled = true
//        })
//    }
}