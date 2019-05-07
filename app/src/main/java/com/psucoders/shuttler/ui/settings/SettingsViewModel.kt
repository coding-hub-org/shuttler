package com.psucoders.shuttler.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psucoders.shuttler.data.firebase.FirebaseSingleton
import com.psucoders.shuttler.data.model.NotificationsModel

class SettingsViewModel : ViewModel() {

    private val logoutStatus = MutableLiveData<Boolean>()
    val timeAhead = MutableLiveData<String>()
    val notificationsEnabled = MutableLiveData<Boolean>()
    private val locationSelected = MutableLiveData<String>()
    val currentToken = MutableLiveData<String>()

    val existingSettings = MutableLiveData<NotificationsModel>()

    val getExistingSettings: LiveData<NotificationsModel>
        get() = existingSettings

    val getLogoutStatus: LiveData<Boolean>
        get() = logoutStatus

    fun decreaseTimeCounter(currentCount: Int) {
        if (currentCount > 0)
            timeAhead.value = (currentCount - 1).toString()
        else {
            timeAhead.value = 0.toString()
        }
        updateSettings()
    }

    fun increaseTimeCounter(currentCount: Int) {
        timeAhead.value = (currentCount + 1).toString()
        updateSettings()
    }

    fun logout() {
        FirebaseSingleton.getInstance().logoutStatus.observeForever { status ->
            logoutStatus.value = status
        }
        FirebaseSingleton.getInstance().logout()
    }

    fun updateNotificationEnabled(enabled: Boolean) {
        notificationsEnabled.value = enabled
        updateSettings()
    }

    fun updateNotificationLocation(location: String) {
        locationSelected.value = location
        updateSettings()
    }

    private fun updateSettings() {
        val tokens = HashMap<String, Boolean>()
        tokens[currentToken.value!!] = notificationsEnabled.value!!
        val notificationsModel = NotificationsModel(tokens, locationSelected.value!!, timeAhead.value!!)
        FirebaseSingleton.getInstance().updateSettings(notificationsModel)
    }

    fun fetchCurrentSettingsFromFirebase() {
        FirebaseSingleton.getInstance().currentSettings.observeForever { settings ->
            val enabled = settings.tokens!![settings.tokens.keys.iterator().next()]
            notificationsEnabled.value = enabled
            existingSettings.value = settings
            timeAhead.value = settings.timeAhead
            locationSelected.value = settings.notifyLocation
        }
        FirebaseSingleton.getInstance().fetchCurrentUserSettingsFromFirebase()
    }
}
