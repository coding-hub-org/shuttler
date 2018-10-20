package com.psucoders.shuttler

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast


class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Get current user
        val currentUser = mAuth.currentUser


        // User is not logged in
        if (currentUser == null) {
            Toast.makeText(this@LoginActivity, "NO USER", Toast.LENGTH_SHORT).show()
            // Add Sign in button
            btnSignIn.setOnClickListener {
                btnSignIn.isEnabled = false
                var email = edtUser.text.toString()
                val password = edtPassword.text.toString()

                // Check if user is a driver
                if (email == "driver@gmail.com") {
                    driver(email, password)
                }

                // Sign in if account exists
                else if (email.contains("@plattsburgh.edu")) {
                    toast("PLATTSBURGH ACCOUNT")
                    toast("FIRST OPTION $email")
                    signIn(email, password)
                }
                else {
                    email+="@plattsburgh.edu"
                    toast("SECOND OPTION $email")
                    signIn(email, password)
                }
            }
        }
        // User is logged in
        else {
            Toast.makeText(this@LoginActivity, "USER LOGGED", Toast.LENGTH_SHORT).show()
            currentUser.reload()
            currentUser.getIdToken(true)
            if (currentUser.email == "driver@gmail.com") {
                val intent = Intent(this, DriversActivity::class.java)
                startActivity(intent)
                finish()
            }
            else if (currentUser.isEmailVerified) {
                Toast.makeText(this@LoginActivity, "VERIFIED", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TrackerActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "NOT VERIFIED", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AuthenticationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign-in is successful
                val currentUser = mAuth.currentUser
                // Check is the user is verified
                // If user is verified, redirect to Tracker activity
                if (currentUser?.isEmailVerified == true) {
                    val intent = Intent(this, TrackerActivity::class.java)
                    intent.putExtra("user", currentUser)
                    startActivity(intent)
                }
                // If user is not verified send to verification activity
                else {
                    Toast.makeText(this@LoginActivity, "SIGN IN SUCCESSFULLY AND NOT VERIFIED", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                }
            }
            // Error signing user in
            else {
                Snackbar.make(loginRoot, "Invalid credentials. Please check your username / password", Snackbar.LENGTH_LONG).show()
                btnSignIn.isEnabled = true
                return@addOnCompleteListener
            }
        }
    }

    // Sign in driver
    private fun driver(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, DriversActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Snackbar.make(loginRoot, it.exception.toString(), Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
