package com.example.lg.deepdreamer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AlarmPopUpActivity extends AppCompatActivity {

    //알람 울릴때 팝업되는 화면

    Button stopService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_pop_up);

        stopService = (Button)findViewById(R.id.bt_StopService);
        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopAlarm = new Intent(getApplicationContext(),AlarmService.class);
                stopService(stopAlarm);//서비스 종료하고
                finish();//엑티비티 종료
            }
        });
    }
}
