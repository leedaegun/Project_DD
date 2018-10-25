package com.example.lg.deepdreamer.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import com.example.lg.deepdreamer.R;
import com.example.lg.deepdreamer.activity.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by lg on 2018-08-27.
 */

public class ManagerNotification {


    private static final int serviceChennelID = 1;
    private static final int serviceAlarmID = 2;
    private static final String channelId = "channel";
    private static final String channelName = "Channel Name";

    public static void alarmNotification(Context context) {
        Notification.Builder builder;

        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);//알림 클릭시 메인화면으로


        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상부터 알림 채널 설정필요

            NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_DEFAULT);
            notificationmanager.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(context,channelId);

        }
        else{
            builder = new Notification.Builder(context);
        }

        builder.setSmallIcon(R.drawable.ic_action_name)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("알람")
                .setContentText("알람 설정한 시간입니다.")
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Bitmap largeIcon = BitmapFactory.decodeResource(Resources.getSystem(),R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);//큰 아이콘
        builder.setColor(Color.RED);//색깔


        notificationmanager.notify(serviceAlarmID, builder.build());}


    public static void operatingService(Context context) {
        Notification.Builder builder;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);//알림 클릭시 메인화면으로


        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상부터 알림 채널 설정필요

            NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_DEFAULT);
            notificationmanager.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(context,channelId);

        }
        else{
            builder = new Notification.Builder(context);
        }

        builder.setSmallIcon(R.drawable.ic_action_name)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("서비스")
                .setContentText("서비스 동작 중입니다.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Bitmap largeIcon = BitmapFactory.decodeResource(Resources.getSystem(),R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);//큰 아이콘
        builder.setColor(Color.RED);//색깔


        notificationmanager.notify(serviceChennelID, builder.build());
    }


}
