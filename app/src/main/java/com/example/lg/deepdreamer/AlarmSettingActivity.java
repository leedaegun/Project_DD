package com.example.lg.deepdreamer;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static com.example.lg.deepdreamer.R.id.set;

public class AlarmSettingActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener{


    private TextView tv;
    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private Calendar mCalendar,currentCalendar;
    //시작 설정 클래스
    private TimePicker mTime;
    private Button alarmDialog;
    private int tmp_year,tmp_month,tmp_date;
    Date date  = new Date();
    /*
     * 통지 관련 맴버 변수
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DBHelper dbHelper = new DBHelper(getApplicationContext(),"DeepDreamerAlarm.db",null,1);

        tv = (TextView)findViewById(R.id.tv);
        //tv.setText("준비 ..");

        //알람 매니저를 취득
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = Calendar.getInstance();
        currentCalendar = Calendar.getInstance();

        mCalendar.setTime(date);
        tmp_date =mCalendar.get(Calendar.DATE);
        tmp_month=mCalendar.get(Calendar.MONTH);
        tmp_year=mCalendar.get(Calendar.YEAR);

        //셋 버튼, 리셋버튼의 리스너를 등록
        setContentView(R.layout.activity_alarm_setting);
        Button b = (Button)findViewById(set);
        b.setOnClickListener (new View.OnClickListener() {
            public void onClick (View v) {

                setAlarm();
                dbHelper.insert(mCalendar.get(mCalendar.MONTH)+1,mCalendar.get(mCalendar.HOUR_OF_DAY),mCalendar.get(mCalendar.MINUTE));//addAlarm(int on, int day, int hour, int min, int vib, String ring)
                //tv.setText(dbHelper.getResult());
                Log.i("년 : ",Integer.toString(mCalendar.get(mCalendar.YEAR)));
                Log.i("달 : ",Integer.toString(mCalendar.get(mCalendar.MONTH)));
                Log.i("분 : ",Integer.toString(mCalendar.get(mCalendar.MINUTE)));
                Log.i("result : ",dbHelper.getResult());
                //dbHelper.check();
            }
        });

        b = (Button)findViewById(R.id.reset);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(AlarmSettingActivity.this,"알람이 해제되었습니다.",Toast.LENGTH_LONG).show();
                resetAlarm();
                dbHelper.allDelete();
            }
        });
        //DATE PICKER DIALOG

        alarmDialog = (Button)findViewById(R.id.btn_alarm_dialog);
        initDate();
        alarmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatePickerDialog dialog = new DatePickerDialog(AlarmSettingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        tmp_year = year;
                        tmp_month= month;
                        tmp_date = date;

                        String msg = String.format("%d 년 %d 월 %d 일", year, month+1, date);
                        alarmDialog.setText(msg);//설정한 날짜로 텍스트 변경
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                            mCalendar.set (year, month, date, mTime.getHour(), mTime.getMinute());//설정한 날짜 저장

                        }else{
                            mCalendar.set (year, month, date, mTime.getCurrentHour(), mTime.getCurrentMinute());//설정한 날짜 저장
                        }

                        Log.i("날짜가 바뀌었습니다", mCalendar.getTime().toString());
                        //Toast.makeText(AlarmSettingActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE));//오늘날짜로 기본 설정

                currentCalendar.setTime(date);
                dialog.getDatePicker().setMinDate(currentCalendar.getTime().getTime());
                dialog.show();

            }
        });

        //타임피커 현재시각으로 초기화
        mTime = (TimePicker)findViewById(R.id.time_picker);
        //버전에따라 다름
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            mTime.setHour(mCalendar.get(Calendar.HOUR_OF_DAY));
            mTime.setMinute(mCalendar.get(Calendar.MINUTE));
        }else{

            mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
            mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        }
        mTime.setOnTimeChangedListener(this);



        //SharedPreferences setting;
        //setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);//로그인정보담을 setting





        Intent intent = new  Intent(this.getIntent());


    }
    //알람의 설정
    private void setAlarm() {
        //tmpCalendar = Calendar.getInstance();
        //tmpCalendar.set(Calendar.YEAR,Calendar.MONTH,Calendar.DATE,Calendar.HOUR_OF_DAY,Calendar.MINUTE,Calendar.SECOND);
        //Log.e("tmpCalender : ",Long.toString(System.currentTimeMillis()));
        //Log.e("before : ",Long.toString(mCalendar.getTimeInMillis()));
        if(false){
            Toast.makeText(AlarmSettingActivity.this,"현재시간보다 전입니다. 알람시간을 다시 설정하세요.",Toast.LENGTH_LONG).show();
        }
        else{
            mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent());
            Toast.makeText(AlarmSettingActivity.this,"알람이 설정되었습니다.",Toast.LENGTH_LONG).show();
            Log.i("알람이 세팅되었습니다", mCalendar.getTime().toString());
            Intent intent = new Intent(AlarmSettingActivity.this,RealMainActivity.class);
            startActivity(intent);
            finish();
        }

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
    private  void initDate(){
        final String initMsg = String.format("%d 년 %d 월 %d 일",tmp_year , tmp_month+1, tmp_date);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // runOnUiThread를 추가하고 그 안에 UI작업을 한다.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alarmDialog.setText(initMsg);
                    }
                });
            }
        }).start();
        Log.i("initMsg : ",initMsg);
        //alarmDialog.setText(initMsg);

        Log.i("처음 시간 설정값",mCalendar.getTime().toString());
        Log.i("intiMsg : ",initMsg);
    }


}
