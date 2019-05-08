package com.psucoders.shuttler.data.model

data class NotificationsModel(val tokens: HashMap<String, Boolean>? = null, val notifyLocation: String = "", val timeAhead: String = "")
