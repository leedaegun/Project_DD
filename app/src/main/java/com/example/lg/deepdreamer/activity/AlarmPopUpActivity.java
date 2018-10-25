package com.example.lg.deepdreamer.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.lg.deepdreamer.R;
import com.example.lg.deepdreamer.service.AlarmService;

public class AlarmPopUpActivity extends AppCompatActivity {

    //알람 울릴때 팝업되는 화면

    Button stopService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_pop_up);
        //팝업세팅설정
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        stopService = (Button)findViewById(R.id.bt_StopService);
        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopAlarm = new Intent(getApplicationContext(),AlarmService.class);              //알람서비스
                //Intent gyroRecordService = new Intent(getApplicationContext(),GyroRecordService.class);//측정서비스

                stopService(stopAlarm);//알람 서비스 종료
                NotificationManager notificationManager= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(2);
                //stopService(gyroRecordService);//측정 서비스 종료

                finish();//엑티비티 종료
            }
        });
    }

}
