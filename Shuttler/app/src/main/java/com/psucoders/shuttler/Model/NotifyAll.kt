package com.psucoders.shuttler.Model

import java.util.*


class NotifyAll(b: HashMap<String, Boolean>, n: String, t: String) {
    var location = t
    var timeAhead = n
    var tokens = b

    fun setNotificationTokens(tokens: HashMap<String, Boolean>) {
        this.tokens = tokens
    }
}