package com.psucoders.shuttler.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.psucoders.shuttler.data.firebase.FirebaseSingleton

class LoginViewModel {

    private val logTag = "Login VM"

    private val _userLoggedIn = MutableLiveData<Boolean>()

    val userLoggedIn : LiveData<Boolean>
        get() = _userLoggedIn

    fun checkIfUserExists(){
        val firebase = FirebaseSingleton.getInstance()
        if(firebase.authInstance.currentUser != null) {
            val currUser = firebase.authInstance.currentUser
            _userLoggedIn.value = true
            Log.d(logTag, "User exists " + currUser!!.email)

        } else {
            Log.d(logTag, "Empty User")
            _userLoggedIn.value = false
        }
    }

    fun loginUser(email: String, password: String) {
        val firebase = FirebaseSingleton.getInstance()
        firebase.loginSuccess().observeForever {
            successful -> _userLoggedIn.value = successful
        }
        firebase.login(email, password)
    }
}