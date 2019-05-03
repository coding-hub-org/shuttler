package com.psucoders.shuttler.ui.login

import android.util.Log
import com.psucoders.shuttler.data.firebase.FirebaseSingleton

class LoginViewModel {

    private val logTag = "Login VM"

    fun checkIfUserExists(){
        val firebase = FirebaseSingleton.getInstance()
        if(firebase.authInstance.currentUser != null) {
            val currUser = firebase.authInstance.currentUser
            Log.d(logTag, "User exists " + currUser!!.email)
        } else {
            Log.d(logTag, "Empty User")
        }
    }
}