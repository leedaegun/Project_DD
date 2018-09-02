package com.example.lg.deepdreamer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.lg.deepdreamer.util.ManagerNotification;
import com.example.lg.deepdreamer.util.WakeLockUtil;

/**
 * Created by lg on 2018-07-04.
 */

public class AlarmReceiver extends BroadcastReceiver
{






    @Override
    public void onReceive(Context context, Intent intent)
    {
        WakeLockUtil.acquireCpuWakeLock(context);//cpu얻고


        Toast.makeText(context, "Alarm Received!", Toast.LENGTH_LONG).show();

        ManagerNotification.alarmNotification(context);//알람 시간 알림


        Intent serviceIntent = new Intent(context,AlarmService.class);

        context.startService(serviceIntent);//알람 서비스 시작



    }




}

