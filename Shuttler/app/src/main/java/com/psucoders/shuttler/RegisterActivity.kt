package com.psucoders.shuttler

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        btnSignUp.setOnClickListener {
            btnSignUp.isEnabled = false
            var email = edtUserSignUp.text.toString()
            val password = edtPasswordSignUp.text.toString()
            // Sign in if account exists
            if (email.contains("@plattsburgh.edu")) {
                toast("PLATTSBURGH ACCOUNT")
                toast("FIRST OPTION $email")
                register(email, password)
            }
            else {
                email+="@plattsburgh.edu"
                toast("SECOND OPTION $email")
                register(email, password)
            }
        }

        btnToLogin.setOnClickListener {
            onBackPressed()
        }
    }

    private fun register(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                Toast.makeText(this@RegisterActivity, "SIGN UP SUCCESSFULLY", Toast.LENGTH_SHORT).show()
                val user = mAuth.currentUser
                user!!.sendEmailVerification().addOnCompleteListener {
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                }
            } else {
                btnSignUp.isEnabled = true
                return@addOnCompleteListener
            }
        }
    }
}
