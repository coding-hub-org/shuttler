package com.psucoders.shuttler.Model


class NotifyAll {

    lateinit var notifyLocation: String
    lateinit var timeAhead: String
    lateinit var tokens: HashMap<String, Boolean>

    fun NotifyAll(){

    }

    fun setNotificationTokens(tokens: HashMap<String, Boolean>) {
        this.tokens = tokens
    }
}