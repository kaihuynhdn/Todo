package com.example.kaihuynh.todo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationAction {

    public static void showNotification(Context context, Class<?> cls, String title, String content, int REQUEST_CODE)
    {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), String.valueOf(REQUEST_CODE));
        Intent ii = new Intent(context.getApplicationContext(), cls);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, ii, 0);


        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.icon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(REQUEST_CODE),
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(REQUEST_CODE, mBuilder.build());
    }
}
