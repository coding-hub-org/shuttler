package com.psucoders.shuttler

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
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

    override fun onStart() {
        super.onStart()

        // Sign up new user
        btnSignUp.setOnClickListener {
            btnSignUp.isEnabled = false
            var email = edtUserSignUp.text.toString()
            val password = edtPasswordSignUp.text.toString()
            // Sign in if account exists
            if (email.contains("@plattsburgh.edu")) {
                toast("PLATTSBURGH ACCOUNT")
                toast("FIRST OPTION $email")
                register(email, password)
            } else {
                email += "@plattsburgh.edu"
                toast("SECOND OPTION $email")
                register(email, password)
            }
        }

        // Return to login activity
        btnToLogin.setOnClickListener {
            onBackPressed()
        }
    }

    private fun register(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
            // If account is created send verification email
            if (it.isSuccessful) {
                // Save user to Database

                Toast.makeText(this@RegisterActivity, "SIGN UP SUCCESSFULLY", Toast.LENGTH_SHORT).show()
                val currUser = mAuth.currentUser

                val user = User()
                user.username = email.substringBeforeLast("@")
                user.email = email
                user.password = password
                val notificationToken = MyFirebaseMessagingService.getToken(applicationContext)
                Log.d("notification token", "is: $notificationToken")
                val tokens = HashMap<String, Boolean>()
                tokens[notificationToken] = true
                user.setNotifications(tokens, "Campus", "5")

                // Use UID to key fro database
                users.child(currUser!!.uid).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Snackbar.make(registerRoot, "Register Successful", Snackbar.LENGTH_SHORT).show()
                    } else {
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
