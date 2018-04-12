package com.lipnus.kumchurk.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lipnus.kumchurk.MainActivity;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.submenu.AlarmActivity;

import java.util.List;

/**
 * Created by Sunpil on 2017-05-27.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";

    Bitmap bitmap = null;

    NotificationManager nm;
    PendingIntent pendingIntent;
    Notification.Builder mBuilder;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "data: " + remoteMessage.getData());
        Log.d(TAG, "notification: " + remoteMessage.getNotification());


        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Log.d(TAG, "FCM_title: " + remoteMessage.getData().get("title"));
            Log.d(TAG, "FCM_text: " + remoteMessage.getData().get("text"));
            Log.d(TAG, "FCM_img: " + remoteMessage.getData().get("img"));

            String text = remoteMessage.getData().get("text");
            String title = remoteMessage.getData().get("title");
            String img = remoteMessage.getData().get("img");

            sendNotification(text, title, img);
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "FCM_Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "FCM_Message Notification Body: " + remoteMessage.getNotification().getBody());

            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle() , null);
        }
    }

    private void sendNotification(final String text, final String title, final String img) {

        //참고:http://itmir.tistory.com/457


        //앱이 실행중인지 확인하고 실행중이면 AlarmActivity, 아니면 IntroActivity로 간다
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).processName.equals("com.lipnus.kumchurk")) {
                pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, AlarmActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                Log.i("FCM_AppList", list.get(i).processName);
            }
        }



        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new Notification.Builder(this);

        mBuilder.setSmallIcon(R.drawable.kumlogo2);
        mBuilder.setTicker("Notification.Builder");
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setNumber(10);
        mBuilder.setContentTitle( title );
        mBuilder.setContentText( text );
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

        mBuilder.setPriority(Notification.PRIORITY_MAX);
        nm.notify(111, mBuilder.build());


    }



}