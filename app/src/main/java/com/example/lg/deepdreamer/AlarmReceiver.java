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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by lg on 2018-07-04.
 */

public class AlarmReceiver extends BroadcastReceiver
{

    private NotificationManager notificationmanager = null;
    PendingIntent pendingIntent;
    String channelId = "channel";
    String channelName = "Channel Name";




    @Override
    public void onReceive(Context context, Intent intent)
    {
        //오류나는거 wakelock인지 확인해볼것
        WakeLockUtil.acquireCpuWakeLock(context);//cpu얻고

        Bundle bundle = intent.getExtras();//알람 설정한 값 받기
        boolean vibe = bundle.getBoolean("isVibe");
        boolean ring = bundle.getBoolean("isRing");
        Uri ringUri =bundle.getParcelable("ringUri");

        Log.i("리시버 전송 받은 isVibe : ",Boolean.toString(vibe));
        Log.i("리시버 전송 받은 ringUri : ",ringUri.toString());//얘가 보낸거랑 다르게 받음




        Toast.makeText(context, "Alarm Received!", Toast.LENGTH_LONG).show();


        notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상부터 알림 채널 설정필요

            NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_DEFAULT);
            notificationmanager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);//채널아이디 파라미터 값으로 안들어감 오ㅑ?

        pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, RealMainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);//알림 클릭시 메인화면으로

        builder.setSmallIcon(R.drawable.ic_action_name)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("알람")
                .setContentText("알람 설정한 시간입니다.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Bitmap largeIcon = BitmapFactory.decodeResource(Resources.getSystem(),R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);//큰 아이콘
        builder.setColor(Color.RED);//색깔


        notificationmanager.notify(1, builder.build());


        Intent serviceIntent = new Intent(context,AlarmService.class);

        serviceIntent.putExtra("isVibe",vibe);
        serviceIntent.putExtra("isRing",ring);
        serviceIntent.putExtra("ringUri",ringUri);

        Log.i("서비스로 전송할 isVibe : ",Boolean.toString(vibe));
        Log.i("서비스로 전송할 isRing : ",Boolean.toString(ring));
        Log.i("서비스로 전송할 uri : ",ringUri.toString());
        //앱 종료후 브로드 캐스트까진 되지만 서비시가 시작안됨
        //서비스 시작하면 다른 기능은 정상 작동함
        context.startService(serviceIntent);//알람 서비스 시작



    }




}

/*
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
        if(b){
            long[] vibrate = {0,100,200,300};//진동설정
            builder.setVibrate(vibrate);
        }
        else{

        }*/

