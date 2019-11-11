package com.codinghub.shuttler.mobile.data.model;

import com.google.gson.annotations.SerializedName;

public class NotificationSentModel {

    @SerializedName("body")
    private String text;

    public String getText() {
        return text;
    }
}
