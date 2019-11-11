package com.codinghub.shuttler.mobile.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codinghub.shuttler.mobile.data.firebase.FirebaseSingleton
import com.codinghub.shuttler.mobile.data.model.NotificationFragmentModel

class NotificationsViewModel : ViewModel() {
    private val _allNotifications = MutableLiveData<ArrayList<NotificationFragmentModel>>()

    val allNotifications: LiveData<ArrayList<NotificationFragmentModel>>
        get() = _allNotifications

    fun fetchNotifications(){
        FirebaseSingleton.getInstance().notifications.observeForever {
            notifications -> _allNotifications.value = notifications
        }
        FirebaseSingleton.getInstance().fetchNotificationsFromFirestore()
    }

}
