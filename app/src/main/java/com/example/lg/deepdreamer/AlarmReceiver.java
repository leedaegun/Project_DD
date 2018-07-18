package com.example.lg.deepdreamer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by lg on 2018-07-04.
 */

public class AlarmReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "Alarm Received!", Toast.LENGTH_LONG).show();
        Intent mServiceintent = new Intent(context,AlarmService.class);
        context.startService(mServiceintent);

        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, RealMainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);//알림 클릭시 메인화면으로

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);//빌더 선언
        builder.setSmallIcon(R.drawable.ic_action_name)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("알람")
                .setContentText("알람 설정한 시간입니다.")
                //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Bitmap largeIcon = BitmapFactory.decodeResource(Resources.getSystem(),R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);//큰 아이콘
        builder.setColor(Color.RED);//색깔

        Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context,RingtoneManager.TYPE_NOTIFICATION);//사운드 설정
        builder.setSound(ringtoneUri);

        long[] vibrate = {0,100,200,300};//진동설정
        builder.setVibrate(vibrate);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상부터 알림 채널 설정필요
            notificationmanager.createNotificationChannel(new NotificationChannel("default","기본 채널",NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationmanager.notify(1, builder.build());






    }



}
