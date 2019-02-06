package com.nsromapa.nsromapa;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import static com.nsromapa.nsromapa.R.mipmap.ic_launcher;

/**
 * Created by SAY on 08/08/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                showNotification(remoteMessage.getData().get("message"));
                // Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }
    }

    private void showNotification(String message) {
        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Nsromapa")
                .setContentText(message)
                .setSmallIcon(ic_launcher)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }
}

