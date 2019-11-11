package com.codinghub.shuttler.mobile.ui.forgotPassword

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codinghub.shuttler.mobile.R
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.jetbrains.anko.toast

class ForgotPassword : AppCompatActivity() {

    lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        forgotPasswordViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ForgotPasswordViewModel::class.java)
    }

    fun handleForgotPassword(v: View) {
        forgotPasswordViewModel.passwordResetLinkSendStatus.observe(this, Observer { reset ->
            if (reset) toast("An email has been sent")
            else toast("Could not send reset email")
        })
        if (edtUsername.text.toString() != "") {
            forgotPasswordViewModel.sendResetPasswordLink(edtUsername.text.toString())
        } else {
            toast("Please enter your username")
        }
    }
}
