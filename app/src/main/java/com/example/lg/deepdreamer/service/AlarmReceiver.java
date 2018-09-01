package com.example.lg.deepdreamer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.lg.deepdreamer.util.WakeLockUtil;
import com.example.lg.deepdreamer.util.ManagerNotification;

/**
 * Created by lg on 2018-07-04.
 */

public class AlarmReceiver extends BroadcastReceiver
{






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
        //builder = new Notification.Builder(context,channelId);//채널아이디 파라미터 값으로 안들어감 오ㅑ?

        ManagerNotification.alarmNotification(context);//알람 시간 알림


        Intent serviceIntent = new Intent(context,AlarmService.class);

        serviceIntent.putExtra("isVibe",vibe);
        serviceIntent.putExtra("isRing",ring);
        serviceIntent.putExtra("ringUri",ringUri);

        Log.i("서비스로 전송할 isVibe : ",Boolean.toString(vibe));
        Log.i("서비스로 전송할 isRing : ",Boolean.toString(ring));
        Log.i("서비스로 전송할 uri : ",ringUri.toString());
        context.startService(serviceIntent);//알람 서비스 시작



    }




}

