package com.psucoders.shuttler.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.psucoders.shuttler.data.model.NotificationFragmentModel

class NotificationsViewModel : ViewModel() {
    private val _allNotifications = MutableLiveData<ArrayList<NotificationFragmentModel>>()

    val allNotifications: LiveData<ArrayList<NotificationFragmentModel>>
        get() = _allNotifications

    fun fetchNotifications(){


    }


}
