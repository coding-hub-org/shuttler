package com.psucoders.shuttler.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.psucoders.shuttler.data.firebase.FirebaseSingleton

class SettingsViewModel : ViewModel() {

    val _logoutStatus = MutableLiveData<Boolean>()

    val _timeAhead = MutableLiveData<String>()

    fun initializeLife() {
        _timeAhead.value = 5.toString()
    }

    val logoutStatus: LiveData<Boolean>
        get() = _logoutStatus

    fun decreaseTimeCounter(currentCount: Int) {
        if (currentCount > 0)
            _timeAhead.value = (currentCount - 1).toString()
        else {
            _timeAhead.value = 0.toString()
        }
    }

    fun increaseTimeCounter(currentCount: Int) {
        _timeAhead.value = (currentCount + 1).toString()
    }

    fun logout() {
        FirebaseSingleton.getInstance().getLogoutStatus().observeForever { status ->
            _logoutStatus.value = status
        }
        FirebaseSingleton.getInstance().logout()
    }
}
