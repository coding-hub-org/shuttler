package com.psucoders.shuttler.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_authentication.*
import androidx.lifecycle.ViewModelProviders


class AuthenticationActivity : AppCompatActivity() {

    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        authenticationViewModel = ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)

        checkIfAlreadyVerified()

    }

    private fun observeEmailAddress() {
        authenticationViewModel.getEmail()
        authenticationViewModel.email.observe(this, Observer { email ->
            val customText =
                    "We've send you a confirmation email to $email, " +
                            "if you didn't receive it please check in spam. If you already " +
                            "authenticated your account please click on the button below"
            confirmationTextView.text = customText
        })


    }

    private fun checkIfAlreadyVerified() {
        authenticationViewModel.checkVerification()
        observeEmailAddress()

        authenticationViewModel.verification.observe(this, Observer { verified ->
            Log.d("Verified status", "" + verified)
            if (verified != null && verified) {
                startActivity(Intent(this, DashboardActivity::class.java))
            }
        })
    }

    fun verifyEmail(v: View) {
        authenticationViewModel.verification.observe(this, Observer { verified ->
            if (verified != null && !verified) {
                Snackbar.make(rootLayoutAuthentication, "Please verify email and try again.", Snackbar.LENGTH_LONG).show()
                authenticationViewModel.resetVerificationCheck()
            }
        })
        authenticationViewModel.checkVerification()
    }

    override fun onResume() {
        super.onResume()
        authenticationViewModel.resetCurrentUser()
    }
}