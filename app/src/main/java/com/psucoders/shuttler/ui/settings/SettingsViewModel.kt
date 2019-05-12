package com.psucoders.shuttler.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psucoders.shuttler.data.firebase.FirebaseSingleton
import com.psucoders.shuttler.data.model.NotificationsModel

class SettingsViewModel : ViewModel() {

    private val logoutStatus = MutableLiveData<Boolean>()
    val notificationsEnabled = MutableLiveData<Boolean>()
    private val locationSelected = MutableLiveData<String>()
    val currentToken = MutableLiveData<String>()
    private val currentUserEmail = MutableLiveData<String>()

    val getLogoutStatus: LiveData<Boolean>
        get() = logoutStatus

    val getCurrentUserEmail: LiveData<String>
        get() = currentUserEmail

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
        val notificationsModel = NotificationsModel(tokens, locationSelected.value!!)
        FirebaseSingleton.getInstance().updateSettings(notificationsModel)
    }

    fun fetchCurrentUser() {
        currentUserEmail.value = FirebaseSingleton.getInstance().authInstance.currentUser!!.email
    }
}
