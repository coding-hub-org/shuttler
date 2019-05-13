package com.psucoders.shuttler.ui.driver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psucoders.shuttler.data.firebase.FirebaseSingleton

class DriverActivityViewModel : ViewModel() {

    private val logoutStatus = MutableLiveData<Boolean>()
    val getLogoutStatus: LiveData<Boolean>
        get() = logoutStatus

    fun logout() {
        FirebaseSingleton.getInstance().logoutStatus.observeForever { status ->
            logoutStatus.value = status
        }
        FirebaseSingleton.getInstance().logout()
    }

}