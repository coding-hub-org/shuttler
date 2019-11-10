package com.psucoders.shuttler.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.util.Pair
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.psucoders.shuttler.R
import kotlinx.android.synthetic.main.login_activity.*
import com.psucoders.shuttler.ui.email.EmailActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)


        val sharedPreferences = getSharedPreferences(EmailActivity.preferences, Context.MODE_PRIVATE)

        Toast.makeText(context, FirebaseAuth.getInstance().currentUser.toString(), Toast.LENGTH_LONG).show()
        FirebaseAuth.getInstance().signOut()

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                    }
                    // Handle the deep link. For example, open the linked
                    // content, or apply promotional credit to the user's
                    // account.
                    // ...

                    // [START_EXCLUDE]
                    // Display deep link in the UI
                    if (deepLink != null) {
                        val emailLink = intent.data!!.toString()
                        if (FirebaseAuth.getInstance().isSignInWithEmailLink(emailLink)) {
                            // Retrieve this from wherever you stored it
                            val email = sharedPreferences.getString(EmailActivity.emailKey, "notFound")!!

                            // The client SDK will parse the code from the link for you.
                            FirebaseAuth.getInstance().signInWithEmailLink(email, emailLink)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val result = task.result
                                            Toast.makeText(context, "Logged In SUCCESS", Toast.LENGTH_LONG).show()
                                            // You can access the new user via result.getUser()
                                            // Additional user info profile *not* available via:
                                            // result.getAdditionalUserInfo().getProfile() == null
                                            // You can check if the user is new or existing:
                                            // result.getAdditionalUserInfo().isNewUser()
                                        } else {
                                            Toast.makeText(context, "Logged In FAILED", Toast.LENGTH_LONG).show()
                                            Log.e("LoginActivity", "Error signing in with email link", task.exception)
                                        }
                                    }
                        }

                    } else {
//                        Toast.makeText(context, "ERRRR, NOT FOUND Found deep link!", Toast.LENGTH_LONG).show()
                    }
                    // [END_EXCLUDE]
                }
                .addOnFailureListener(this) { e -> Log.w("LoginActivity", "getDynamicLink:onFailure", e) }

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