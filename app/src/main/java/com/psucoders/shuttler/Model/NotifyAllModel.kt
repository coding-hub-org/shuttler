package com.psucoders.shuttler.Model


class NotifyAllModel {

    var uid:String = ""
    lateinit var rawData: NotifyUser

    fun NotifyAllModel(){}

    fun NotifyAllModel(uid: String, data: NotifyUser){
        this.uid = uid
        this.rawData = data
    }

    fun fetchRawData(): NotifyUser{
        return rawData
    }

    fun fetchUid(): String{
        return uid
    }

}