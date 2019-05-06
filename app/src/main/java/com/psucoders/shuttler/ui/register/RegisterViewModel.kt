package com.psucoders.shuttler.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psucoders.shuttler.data.firebase.FirebaseSingleton

class RegisterViewModel : ViewModel() {

    private val _valid = MutableLiveData<Boolean>()
    private val _fcmToken = MutableLiveData<String>()

    val getFcmToken: LiveData<String>
        get() = _fcmToken

    val valid: LiveData<Boolean>
        get() = _valid

    private val _registrationSuccess = MutableLiveData<Boolean>()

    val registrationSuccess: LiveData<Boolean>
        get() = _registrationSuccess

    fun handleRegister(prevEmail: String, password: String) {

        val email = if (prevEmail.contains("@plattsburgh.edu")) prevEmail else "$prevEmail@plattsburgh.edu"

        if (validateInput(email, password)) {
            val firebase = FirebaseSingleton.getInstance()

            firebase.registrationSuccess.observeForever { success ->
                _registrationSuccess.value = success
            }

            firebase.fcmToken.observeForever { token ->
                _fcmToken.value = token
            }

            firebase.register(email, password, getUsernameFromEmail(email))
        } else {
            _valid.value = false
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        // Handle email validation
        return when {
            email.isEmpty() || password.isEmpty() -> false
            password.length < 6 -> false
            else -> true
        }
    }

    fun resetValidity() {
        _valid.value = null
    }

    private fun getUsernameFromEmail(email: String): String {
        return email.substringBeforeLast('@')
    }

}
