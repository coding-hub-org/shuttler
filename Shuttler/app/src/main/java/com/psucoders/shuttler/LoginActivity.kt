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
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser

        if (currentUser == null) {
            btnSignIn.setOnClickListener {
                val email = edtUser.text.toString()
                val password = edtPassword.text.toString()
                signInUp(email, password)
            }
        }
        else {
            Toast.makeText(this@LoginActivity, currentUser.email, Toast.LENGTH_SHORT).show()
            currentUser.reload()
            currentUser.getIdToken(true)
            if (currentUser.isEmailVerified) {
                //Log.d("IS VERIFIED: ", currentUser.isEmailVerified.toString())
                Toast.makeText(this@LoginActivity, "VERIFIED", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TrackerActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                //Log.d("IS VERIFIED: ", currentUser.isEmailVerified.toString())
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
                Log.d("FIREBASE REGISTRATION", "success to create user UID: ${it.result.user.uid}")
                val user = mAuth.currentUser
                user!!.sendEmailVerification().addOnCompleteListener {
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
