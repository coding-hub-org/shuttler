package com.psucoders.shuttler.ui.authentication

import android.content.Context
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
import com.psucoders.shuttler.ui.email.EmailActivity


class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        val preferences = getSharedPreferences(EmailActivity.preferences, Context.MODE_PRIVATE)
        val email = preferences.getString(EmailActivity.emailKey, "NotFound")!!

        val customText =
                "We've send you a confirmation email to $email, " +
                        "if you didn't receive it please check in spam. If you already " +
                        "authenticated your account please click on the button below"
        confirmationTextView.text = customText
    }


    fun editEmail(v: View) {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
    }
}