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

        // Sing out user
       // mAuth.signOut()

        btnSignIn.setOnClickListener {
            val email = edtUser.text.toString()
            val password = edtPassword.text.toString()
            signIn(email, password)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    private fun signIn(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                Log.d("FIREBASE REGISTRATION", "success to create user UID: ${it.result.user.uid}")
                val user = mAuth.currentUser
                user!!.sendEmailVerification().addOnCompleteListener {
                    Toast.makeText(this@LoginActivity, "Verify Email", Toast.LENGTH_SHORT).show()
                }
            }

            else {
                Toast.makeText(this@LoginActivity, "FAIL TO CREATE ACCOUNT", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }
        }

    }
}
