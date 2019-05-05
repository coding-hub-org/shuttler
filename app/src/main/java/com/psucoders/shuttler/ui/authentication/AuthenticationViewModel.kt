package com.psucoders.shuttler.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.psucoders.shuttler.data.firebase.FirebaseSingleton

class AuthenticationViewModel {
    private val _email = MutableLiveData<String>()
    val logTag = "Authentication VM"

    val email: LiveData<String>
        get() = _email

    private val _verification = MutableLiveData<Boolean>()

    val verification: LiveData<Boolean>
        get() = _verification

    fun getEmail() {
        _email.value = FirebaseSingleton.getInstance().authInstance.currentUser!!.email
    }

    fun checkVerification() {
        val verified = FirebaseSingleton.getInstance().authInstance.currentUser!!.isEmailVerified
        _verification.value = verified

        Log.d(logTag, "verification status: $verified")
    }

    fun resetVerificationCheck() {
        _verification.value = null
    }
}
