package com.psucoders.shuttler.utils.notifications;

import com.psucoders.shuttler.data.model.NotificationSentModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NotificationService {

    @GET("sendNotification")
    Call<NotificationSentModel> sendNotification(@Query("location") String location);
}
