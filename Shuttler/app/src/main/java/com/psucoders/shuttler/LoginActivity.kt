package com.psucoders.shuttler

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser




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
            // Add Sign in / Sign Up button
            btnSignIn.setOnClickListener {
                val email = edtUser.text.toString()
                val password = edtPassword.text.toString()
                signInUp(email, password)
            }
        }
        // User is logged in
        else {
            Toast.makeText(this@LoginActivity, "USER LOGGED", Toast.LENGTH_SHORT).show()
            currentUser.reload()
            currentUser.getIdToken(true)
            if (currentUser.isEmailVerified) {
                Toast.makeText(this@LoginActivity, "VERIFIED", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TrackerActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this@LoginActivity, "NOT VERIFIED", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AuthenticationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun signInUp(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val user = mAuth.currentUser
                user!!.sendEmailVerification().addOnCompleteListener {
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                }
            }
            else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful) {
                        val currentUser = mAuth.currentUser
                        if (currentUser?.isEmailVerified == true) {
                            Toast.makeText(this@LoginActivity, "SIGN IN SUCCESSFULLY AND VERIFIED", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, TrackerActivity::class.java)
                            intent.putExtra("user", currentUser)
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(this@LoginActivity, "SIGN IN SUCCESSFULLY AND NOT VERIFIED", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, AuthenticationActivity::class.java)
                            startActivity(intent)
                        }

                    }
                    else {
                        Toast.makeText(this@LoginActivity, "FAIL TO CREATE ACCOUNT", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                }
            }
        }

    }
}
