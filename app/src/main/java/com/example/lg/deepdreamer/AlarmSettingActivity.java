package com.example.lg.deepdreamer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmSettingActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener{
    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private Calendar mCalendar;
    //시작 설정 클래스
    private TimePicker mTime;
    private Button alarmDialog;
    private int tmp_year,tmp_month,tmp_date;
    /*
     * 통지 관련 맴버 변수
     */
    private NotificationManager mNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //통지 매니저를 취득
        mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        //알람 매니저를 취득
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = Calendar.getInstance();

        tmp_year=mCalendar.get(Calendar.YEAR);
        tmp_month=mCalendar.get(Calendar.MONTH);
        tmp_date=mCalendar.get(Calendar.DATE);

        //String initMsg = String.format("%d 년 %d 월 %d 일", mCalendar.get(Calendar.YEAR) , mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE));
        //alarmDialog.setText(initMsg);

        Log.i("처음 시간 설정값",mCalendar.getTime().toString());
        //셋 버튼, 리셋버튼의 리스너를 등록
        setContentView(R.layout.activity_alarm_setting);
        Button b = (Button)findViewById(R.id.set);
        b.setOnClickListener (new View.OnClickListener() {
            public void onClick (View v) {
                Toast.makeText(AlarmSettingActivity.this,"알람이 설정되었습니다.",Toast.LENGTH_LONG).show();
                setAlarm();
                Intent intent = new Intent(AlarmSettingActivity.this,RealMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        b = (Button)findViewById(R.id.reset);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(AlarmSettingActivity.this,"알람이 해제되었습니다.",Toast.LENGTH_LONG).show();
                resetAlarm();
            }
        });
        //DATE PICKER DIALOG

        alarmDialog = (Button)findViewById(R.id.btn_alarm_dialog);

        alarmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = new DatePickerDialog(AlarmSettingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        tmp_year = year;
                        tmp_month= month;
                        tmp_date = date;

                        String msg = String.format("%d 년 %d 월 %d 일", year, month+1, date);
                        alarmDialog.setText(msg);//설정한 날짜로 텍스트 변경
                        mCalendar.set (year, month+1, date, mTime.getCurrentHour(), mTime.getCurrentMinute());//설정한 날짜 저장
                        Log.i("날짜가 바뀌었습니다", mCalendar.getTime().toString());
                        //Toast.makeText(AlarmSettingActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE));//오늘날짜로 기본 설정

                dialog.show();

            }
        });

        //타임피커 현재시각으로 초기화
        mTime = (TimePicker)findViewById(R.id.time_picker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setOnTimeChangedListener(this);

        TextView tv = (TextView)findViewById(R.id.tv);

        SharedPreferences setting;
        setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);//로그인정보담을 setting

        tv.setText(setting.getString("success_ID",""));



        Intent intent = new  Intent(this.getIntent());


    }
    //알람의 설정
    private void setAlarm() {
        mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent());
        Log.i("알람이 세팅되었습니다", mCalendar.getTime().toString());
    }

    //알람의 해제
    private void resetAlarm() {
        mManager.cancel(pendingIntent());
        Log.i("알람이 해제되었습니다", mCalendar.getTime().toString());
    }
    //알람의 설정 시각에 발생하는 인텐트 작성
    private PendingIntent pendingIntent() {
        Intent i = new Intent(AlarmSettingActivity.this,AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        return pi;
    }
    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged (TimePicker view, int hourOfDay, int minute) {
        mCalendar.set (tmp_year, tmp_month, tmp_date, hourOfDay, minute);
        Log.i("시간이 바뀌었습니다",mCalendar.getTime().toString());
    }


}
