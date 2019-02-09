package com.psucoders.shuttler.Model

import java.util.*


class NotifyUser {
    lateinit var username: String
    lateinit var email: String
    lateinit var password: String
    lateinit var notifac: NotifyAll

    fun NotifyUser(){

    }

    fun  NotifyUser(username: String, email: String, password: String) {

    }

    fun setNotifications(b: HashMap<String, Boolean>, n: String, t: String) {

        this.notifac = NotifyAll(b, n, t)
    }
}