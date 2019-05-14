package com.psucoders.shuttler.utils.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.psucoders.shuttler.R;
import com.psucoders.shuttler.data.model.NotificationSentModel;
import com.psucoders.shuttler.ui.login.LoginActivity;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default");
        notificationBuilder.setContentTitle("The shuttle is here");
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.drawable.shuttler_notif_icon);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    public static void sendNotification(String locationName) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://us-central1-shuttler-p001.cloudfunctions.net/").addConverterFactory(GsonConverterFactory.create()).build();
        NotificationService notificationService = retrofit.create(NotificationService.class);
        Call<NotificationSentModel> call = notificationService.sendNotification(locationName);

        call.enqueue(new Callback<NotificationSentModel>() {
            @Override
            public void onResponse(@NotNull Call<NotificationSentModel> call, @NotNull Response<NotificationSentModel> response) {
                Log.d("test", "test$response");
            }

            @Override
            public void onFailure(@NotNull Call<NotificationSentModel> call, Throwable t) {
                Log.d("test", "test${t.toString()}");
            }
        });
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", token).apply();
    }

    public void setNewToken(String token, Context context) {
        Log.d(TAG, "Token: " + token);
        context.getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", token).apply();
    }
}