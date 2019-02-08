package com.psucoders.shuttler

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
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

        if (currentUser == null) {
            // User is not logged in

            // TODO: Delete toast for deployment
            Toast.makeText(this@LoginActivity, "NO USER", Toast.LENGTH_SHORT).show()

            // Add Sign in button
            btnSignIn.setOnClickListener {
                btnSignIn.isEnabled = false
                var email = edtUser.text.toString()
                val password = edtPassword.text.toString()

                if (validateInput(email, password)) {
                    // Check if user is the driver
                    when {
                        email == "driver@gmail.com" -> signInDriver(email, password)

                        // Sign in if account exists
                        email.contains("@plattsburgh.edu") -> {
                            // TODO: Delete toast for deployment
                            toast("PLATTSBURGH ACCOUNT")
                            toast("FIRST OPTION $email")
                            signIn(email, password)
                        }
                        else -> {
                            email+="@plattsburgh.edu"
                            // TODO: Delete toast for deployment
                            toast("SECOND OPTION $email")
                            signIn(email, password)
                        }
                    }
                } else {
                    Snackbar.make(loginRoot, "Invalid credentials. Please check your username / password", Snackbar.LENGTH_LONG).show()
                    btnSignIn.isEnabled = true
                }
            }
        }
        // User is logged in
        else {
            // TODO: Delete toast for deployment
            Toast.makeText(this@LoginActivity, "USER LOGGED", Toast.LENGTH_SHORT).show()
            currentUser.reload()
            currentUser.getIdToken(true)
            when {
                // TODO: Delete toast for deployment
                // TODO: Change user Gmail acct.
                currentUser.email == "driver@gmail.com" -> {
                    val intent = Intent(this, DriversActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                currentUser.isEmailVerified -> {
                    // TODO: Delete toast for deployment
                    Toast.makeText(this@LoginActivity, "VERIFIED", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, TrackerActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    // TODO: Delete toast for deployment
                    Toast.makeText(this@LoginActivity, "NOT VERIFIED", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    /**
     * Validate user email and password
     * @param email User email / username
     * @param password User password
     * @return true if input is valid false otherwise.
     */
    private fun validateInput(email: String, password: String) : Boolean {
        // Handle email validation
        return when {
            email.isEmpty() || password.isEmpty() -> false
            password.length < 6 -> false
            else -> true
        }
    }

    /**
     * Send user to registration activity
     * @param v Button (View) to be click
     * @return void.
     */
    fun registerUser(v: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    /**
     * This is the sign in method for the users / students
     * @param email User plattsburgh email.
     * @param email User acct password.
     * @return void.
     */
    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign-in is successful
                val currentUser = mAuth.currentUser
                // Check is the user is verified
                if (currentUser?.isEmailVerified == true) {
                    // If user is verified, send to Tracker / Map activity
                    val intent = Intent(this, TrackerActivity::class.java)
                    intent.putExtra("user", currentUser)
                    startActivity(intent)
                }
                else {
                    // If user is not verified send to verification activity
                    // TODO: Delete toast for deployment
                    Toast.makeText(this@LoginActivity, "SIGN IN SUCCESSFULLY AND NOT VERIFIED", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                }
            }
            else {
                // Error signing user in
                Snackbar.make(loginRoot, "Invalid credentials. Please check your username / password", Snackbar.LENGTH_LONG).show()
                btnSignIn.isEnabled = true
                return@addOnCompleteListener
            }
        }
    }

    /**
     * This is the sign in method for the driver
     * @param email Driver's email.
     * @param email Driver's acct password.
     * @return void.
     */
    private fun signInDriver(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, DriversActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Snackbar.make(loginRoot, it.exception.toString(), Snackbar.LENGTH_LONG).show()
                btnSignIn.isEnabled = true
            }
        }
    }
}
