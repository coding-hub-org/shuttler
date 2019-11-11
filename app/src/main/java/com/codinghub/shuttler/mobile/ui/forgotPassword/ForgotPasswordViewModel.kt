package com.codinghub.shuttler.mobile.ui.forgotPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codinghub.shuttler.mobile.data.firebase.FirebaseSingleton


class ForgotPasswordViewModel: ViewModel() {

    private val _passwordResetLinkSendStatus = MutableLiveData<Boolean>()

    val passwordResetLinkSendStatus: LiveData<Boolean>
        get() = _passwordResetLinkSendStatus

    fun sendResetPasswordLink(prevEmail: String){
        val email = if (prevEmail.contains("@plattsburgh.edu")) prevEmail else "$prevEmail@plattsburgh.edu"
        val firebase = FirebaseSingleton.getInstance()
        firebase.passwordResetEmailStatus.observeForever { resetStatus ->
            _passwordResetLinkSendStatus.value = resetStatus
        }
        firebase.sendPasswordResetLink(email)
    }
}