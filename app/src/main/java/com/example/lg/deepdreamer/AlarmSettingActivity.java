package com.example.lg.deepdreamer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static com.example.lg.deepdreamer.R.id.set;

public class AlarmSettingActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener{

    public SharedPreferences setting;

    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private Calendar mCalendar,currentCalendar;
    //시작 설정 클래스
    private TimePicker mTime;
    private Button alarmDialog,repeatDialog;
    private int tmp_year,tmp_month,tmp_date;
    Date date  = new Date();
    private int selectedRepeatTime;//알람 주기
    private String selectedTime = null;//넘겨줄 알람주기
    private Switch repeatSwitch,vibeSwitch;
    Boolean isVibe;
    /*
     * 통지 관련 맴버 변수
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DBHelper dbHelper = new DBHelper(getApplicationContext(),"DeepDreamerAlarm.db",null,1);

        //tv = (TextView)findViewById(R.id.tv);
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

        //확인 버튼, 리셋버튼의 리스너를 등록
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
        //취소버튼
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
        initDate();//버튼 현재날짜로
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

        setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);//로그인정보담을 setting

        repeatDialog = (Button)findViewById(R.id.bt_repeat);
        if(setting.getString("repeatTime","")!=null){repeatDialog.setText(setting.getString("repeatTime",""));}
        repeatSwitch = (Switch)findViewById(R.id.sw_repeatAlarm);
        repeatSwitch.setChecked(setting.getBoolean("repeatTimeBoolean",false));
        repeatDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] repeatTime = new String[]{"5분","10분","15분","30분"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(AlarmSettingActivity.this);
                dialog.setTitle("간격")
                        .setSingleChoiceItems(repeatTime, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //선택한 아이템
                                selectedRepeatTime =i;
                            }
                        }).setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        repeatDialog.setText("다시 울림("+repeatTime[selectedRepeatTime]+")");
                        selectedTime = repeatTime[selectedRepeatTime];
                        Log.i("selectedTime : ",selectedTime);
                    }
                }).setCancelable(false)
                .show();
            }
        });
        repeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    //스위치 체크되어있을때
                    repeatDialog.setEnabled(true);
                }
                else{
                    //스위치 해제
                    repeatDialog.setEnabled(false);
                    selectedRepeatTime =0;
                }
            }
        });
        vibeSwitch = (Switch)findViewById(R.id.sw_vibe);
        vibeSwitch.setChecked(setting.getBoolean("isVibe",false));
        vibeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    //스위치 체크되어있을때
                    isVibe=true;
                }
                else{
                    //스위치 해제
                    isVibe=false;
                }
            }
        });





    }
    //알람의 설정
    private void setAlarm() {
        currentCalendar = Calendar.getInstance();
        //tmpCalendar.set(Calendar.YEAR,Calendar.MONTH,Calendar.DATE,Calendar.HOUR_OF_DAY,Calendar.MINUTE,Calendar.SECOND);
        //Log.e("tmpCalender : ",Long.toString(System.currentTimeMillis()));
        //Log.e("before : ",Long.toString(mCalendar.getTimeInMillis()));
        if(mCalendar.compareTo(currentCalendar)<=0){
            Toast.makeText(AlarmSettingActivity.this,"현재시간보다 전입니다. 알람시간을 다시 설정하세요.",Toast.LENGTH_LONG).show();
        }
        else{
            mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent());
            if(repeatSwitch.isChecked()){//반복 주기 설정 했을때
                //라디오 버튼으로 직접 설정안하면 강종 무조건 설정해야됨 수정필요!!
                int t = Integer.parseInt(selectedTime.substring(0,selectedTime.length()-1));//반복 주기
                Log.i("자른거 : ",selectedTime.substring(0,selectedTime.length()-1));
                Toast.makeText(AlarmSettingActivity.this,t+"분 반복 알람",Toast.LENGTH_LONG).show();
                //mManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), 1000*t,pendingIntent());//반복주기 설정
            }

            int date = mCalendar.get(mCalendar.DATE)-currentCalendar.get(Calendar.DATE);
            int hour = mCalendar.get(mCalendar.HOUR)-currentCalendar.get(Calendar.HOUR);
            int minute = mCalendar.get(mCalendar.MINUTE)-currentCalendar.get(Calendar.MINUTE);

            if(minute<0){
                minute = 60 + minute;
                hour = hour -1;
            }
            if(hour<0){
                hour = 24 + hour;
                date = date -1;
            }

            if(date==0){
                if(hour==0){
                    Toast.makeText(AlarmSettingActivity.this,minute+"분 후 알람이 울립니다.",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(AlarmSettingActivity.this,date+"시간" +minute+"분 후 알람이 울립니다.",Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(AlarmSettingActivity.this,date+"일"+hour+"시간" +minute+"분 후 알람이 울립니다.",Toast.LENGTH_LONG).show();
            }

            Log.i("알람이 세팅되었습니다", mCalendar.getTime().toString());
            Intent intent = new Intent(AlarmSettingActivity.this,RealMainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("alarmTime",String.format("%d 시 %d 분",mCalendar.get(mCalendar.HOUR) , mCalendar.get(mCalendar.MINUTE)));
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK,intent);
            //startActivity(intent);
            finish();
        }

    }

    //알람의 해제
    private void resetAlarm() {
        mManager.cancel(pendingIntent());
        Intent intent = new Intent(AlarmSettingActivity.this,RealMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("alarmTime","알람 설정");//알람 해제 했을때 다시 알람 설정으로
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK,intent);
        finish();

        Log.i("알람이 해제되었습니다", mCalendar.getTime().toString());
    }
    //알람의 설정 시각에 발생하는 인텐트 작성
    private PendingIntent pendingIntent() {
        Intent i = new Intent(AlarmSettingActivity.this,AlarmReceiver.class);
        i.putExtra("isVibe",isVibe);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        return pi;
    }
    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged (TimePicker view, int hourOfDay, int minute) {
        mCalendar.set (tmp_year, tmp_month, tmp_date, hourOfDay, minute);
        Log.i("시간이 바뀌었습니다",mCalendar.getTime().toString());
    }

    private  void initDate(){
        //현재날짜로 설정
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

    @Override
    protected void onStop() {
        super.onStop();
        setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();

        // if autoLogin Checked, save values
        editor.putString("repeatTime", repeatDialog.getText().toString());
        editor.putBoolean("repeatTimeBoolean",repeatSwitch.isChecked());
        editor.putBoolean("isVibe",vibeSwitch.isChecked());

        editor.commit();
    }
}
