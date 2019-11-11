package com.codinghub.shuttler.mobile.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.codinghub.shuttler.mobile.R
import com.codinghub.shuttler.mobile.ui.driverLogin.DriverLoginAcitivity
import com.codinghub.shuttler.mobile.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.login_activity.*
import com.codinghub.shuttler.mobile.ui.email.EmailActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private val context = this
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        sharedPreferences = getSharedPreferences(EmailActivity.preferences, Context.MODE_PRIVATE)

        Toast.makeText(context, FirebaseAuth.getInstance().currentUser.toString(), Toast.LENGTH_LONG).show()

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
                                            val editor2 = sharedPreferences.edit()
                                            editor2.putBoolean(EmailActivity.isSignedInKey, true)
                                            editor2.apply()
                                            FirebaseAuth.getInstance().currentUser!!.delete()
                                            FirebaseAuth.getInstance().signInAnonymously()
                                            Log.d("LoginActivity", result.toString())
                                            Toast.makeText(context, "Logged In SUCCESS $result", Toast.LENGTH_LONG).show()
                                            // You can access the new user via result.getUser()
                                            // Additional user info profile *not* available via:
                                            // result.getAdditionalUserInfo().getProfile() == null
                                            // You can check if the user is new or existing:
                                            // result.getAdditionalUserInfo().isNewUser()
                                            redirectToDashboard()
                                        } else {
                                            Toast.makeText(context, "Logged In FAILED", Toast.LENGTH_LONG).show()
                                            Log.e("LoginActivity", "Error signing in with email link", task.exception)
                                        }
                                    }
                        }
                    } else {
//                        Toast.makeText(context, "ERRRR, NOT FOUND Found deep link!", Toast.LENGTH_LONG).show()
                    }
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

        driverButton.setOnClickListener {
            startActivity(Intent(this, DriverLoginAcitivity::class.java))
        }
    }

    private fun redirectToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (sharedPreferences.getBoolean(EmailActivity.isSignedInKey, false)) {
            redirectToDashboard()
        }
    }
}