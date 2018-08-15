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
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by lg on 2018-07-04.
 */

public class AlarmReceiver extends BroadcastReceiver
{

    private NotificationManager notificationmanager = null;
    PendingIntent pendingIntent;

    PowerManager pm;
    PowerManager.WakeLock wl = null;
    @Override
    public void onReceive(Context context, Intent intent)
    {/*
        pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) { // 스크린이 켜져 있지 않으면 켠다
            wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NabakAlarm");
            wl.acquire();
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//										WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        }*/
        Toast.makeText(context, "Alarm Received!", Toast.LENGTH_LONG).show();


        notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, RealMainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);//알림 클릭시 메인화면으로

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

        //진동모드나 사일런트 모드일 때도 벨 울리게

        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT //: 사일런트 모드일 경우(값0)
                || mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){ //: 진동모드일 경우(값1))
            int maxVolume =  mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
            //for(int i=1; i<=maxVolume; i++){
            //      mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING,  AudioManager.ADJUST_RAISE, 0);
            //  }
        }
        long[] vibrate = {0,100,200,300};//진동설정
        builder.setVibrate(vibrate);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상부터 알림 채널 설정필요
            notificationmanager.createNotificationChannel(new NotificationChannel("default","기본 채널",NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationmanager.notify(1, builder.build());






    }



}
