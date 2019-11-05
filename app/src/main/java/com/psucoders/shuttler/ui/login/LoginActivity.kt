package com.psucoders.shuttler.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.psucoders.shuttler.ui.driver.DriverActivity
import com.psucoders.shuttler.ui.forgotPassword.ForgotPassword


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val actionCodeSettings = ActionCodeSettings.newBuilder()
                // URL you want to redirect back to. The domain (www.example.com) for this
                // URL must be whitelisted in the Firebase Console.
                .setUrl("shuttler-p001.firebaseapp.com")
                // This must be true
                .setHandleCodeInApp(true)
                .setAndroidPackageName(
                        "com.psucoders.shuttler",
                        true, /* installIfNotAvailable */
                        "21" /* minimumVersion */)
                .build()

//        val auth = FirebaseAuth.getInstance()

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                        Toast.makeText(this, "Found deep link!", Toast.LENGTH_LONG).show()
                    }

//                    // Display deep link in the UI
//                    if (deepLink != null) {
//                        Toast.makeText(this, "Found deep link!", Toast.LENGTH_LONG).show()
//                    } else {
//                        Toast.makeText(this, "Not Found deep link!", Toast.LENGTH_LONG).show()
//                        Log.d("LoginActivity", "getDynamicLink: no link found")
//                    }
                }
                .addOnFailureListener(this) { e -> Log.w("LoginActivity", "getDynamicLink:onFailure", e) }

        loginFAB.setOnClickListener {
            Toast.makeText(this, "Click", Toast.LENGTH_LONG).show()
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