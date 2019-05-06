package com.psucoders.shuttler.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psucoders.shuttler.data.firebase.FirebaseSingleton

class SettingsViewModel : ViewModel() {

    val _logoutStatus = MutableLiveData<Boolean>()
    val _timeAhead = MutableLiveData<String>()
    val _notificationsEnabled = MutableLiveData<Boolean>()
    val _locationSelected = MutableLiveData<String>()

    val locationSelected: LiveData<String>
        get() = _locationSelected

    val notificationsEnabled: LiveData<Boolean>
        get() = _notificationsEnabled

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
        updateTimeAhead()
    }

    fun increaseTimeCounter(currentCount: Int) {
        _timeAhead.value = (currentCount + 1).toString()
        updateTimeAhead()
    }

    fun logout() {
        FirebaseSingleton.getInstance().logoutStatus.observeForever { status ->
            _logoutStatus.value = status
        }
        FirebaseSingleton.getInstance().logout()
    }

    fun updateNotificationEnabled(enabled: Boolean) {
        _notificationsEnabled.value = true
//        FirebaseSingleton.getInstance().updateSettings()
    }

    fun updateNotificationLocation(location: String) {
        _locationSelected.value = location
    }

    fun updateTimeAhead() {

    }

    fun fetchNewToken() {

    }
}
