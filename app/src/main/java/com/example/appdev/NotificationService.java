package com.example.appdev;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// Service used to notify the user when they get a message.
public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Get the information of the notification we received.
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();

        // Set the correct notification channel.
        String CHANNEL_ID = "MESSAGE";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Message Notification",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        // Build a notification based no the received message.
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        // Notify the user.
        NotificationManagerCompat.from(this).notify(1, notification.build());
        super.onMessageReceived(remoteMessage);
    }
}
