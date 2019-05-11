package com.psucoders.shuttler;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NotificationService {

    @GET("/sendNotification")
    void sendNotification(@Query("location") String location);
}
