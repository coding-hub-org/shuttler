package com.psucoders.shuttler.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psucoders.shuttler.data.firebase.FirebaseSingleton

class LoginViewModel : ViewModel() {

    private val logTag = "Login VM"

    private val _userLoggedIn = MutableLiveData<Boolean>()
    private val _validFields = MutableLiveData<Boolean>()
    private val _isDriver = MutableLiveData<Boolean>()

    val userLoggedIn: LiveData<Boolean>
        get() = _userLoggedIn

    val validFields: LiveData<Boolean>
        get() = _validFields

    val isDriver: LiveData<Boolean>
        get() = _isDriver

    fun checkIfUserExists() {
        val firebase = FirebaseSingleton.getInstance()
        _userLoggedIn.value = firebase.authInstance.currentUser != null
    }

    fun checkIfUserIsDriver() {
        val firebase = FirebaseSingleton.getInstance()
        _isDriver.value = firebase.authInstance.currentUser!!.email == "driver@gmail.com"
    }

    fun loginUser(email: String, password: String) {
        val firebase = FirebaseSingleton.getInstance()
        firebase.loginSuccess().observeForever { successful ->
            _validFields.value = successful
        }
        if (validateAndLogin(email, password)) {
            firebase.login(email, password)
        } else {
            _validFields.value = false
        }
    }

    private fun validateAndLogin(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> false
            password.length < 6 -> false
            else -> true
        }
    }

    fun resetValidity() {
        _validFields.value = null
    }
}