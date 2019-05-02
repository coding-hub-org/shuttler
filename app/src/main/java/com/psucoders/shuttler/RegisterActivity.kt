package com.psucoders.shuttler

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.psucoders.shuttler.Model.User
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var users: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        users = db.getReference("Users")
    }

    /**
     * Handle user registration information
     * @param v Register button
     * @return void.
     */
    fun handleRegister(v: View) {
        v.isEnabled =false
        var email = edtUserSignUp.text.toString()
        val password = edtPasswordSignUp.text.toString()

        // Check if user used plattsburgh email or username for registration
        email = if (email.contains("@plattsburgh.edu")) email else "$email@plattsburgh.edu"
        if (validateInput(email, password)) {
            processRegister(email, password)
        } else {
            v.isEnabled =true
            Snackbar.make(registerRoot, "Username and/or password don't meet requirement", Snackbar.LENGTH_LONG).show()
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
    fun returnToLogin(v: View) {
        onBackPressed()
    }

    /**
     * Process user registration information
     * @param email User plattsburgh email / username
     * @param password User password
     * @return void.
     */
    private fun processRegister(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
            // If account is created send verification email
            if (it.isSuccessful) {
                // Save user to Database
                // TODO: Delete toast for deployment
                Toast.makeText(this@RegisterActivity, "SIGN UP SUCCESSFULLY", Toast.LENGTH_SHORT).show()

                val currUser = mAuth.currentUser
                val user = User()
                user.username = email.substringBeforeLast("@")
                user.email = email
                user.password = password

                val notificationToken = MyFirebaseMessagingService.getToken(applicationContext)

                // TODO: Delete log for deployment
                Log.d("notification token", "is: $notificationToken")
                val tokens = HashMap<String, Boolean>()
                tokens[notificationToken] = true
                user.setNotifications(tokens, "Campus", "5")

                // Use UID to key from database
                users.child(currUser!!.uid).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Snackbar.make(registerRoot, "Register Successful", Snackbar.LENGTH_SHORT).show()
                    }
                    else {
                        Snackbar.make(registerRoot, "Failed ${it.exception}", Snackbar.LENGTH_SHORT).show()
                    }
                }

                currUser.sendEmailVerification().addOnCompleteListener {
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                }
            }
            // Stay in the same activity
            else {
                btnSignUp.isEnabled = true
                return@addOnCompleteListener
            }
        }
    }
}
