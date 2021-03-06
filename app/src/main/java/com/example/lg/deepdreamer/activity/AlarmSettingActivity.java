package com.example.lg.deepdreamer.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
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

import com.example.lg.deepdreamer.DB.DBHelper;
import com.example.lg.deepdreamer.R;
import com.example.lg.deepdreamer.service.AlarmReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.example.lg.deepdreamer.R.id.set;

public class AlarmSettingActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener {

    public static final int REQUEST_CODE_RINGTONE = 10005;//벨소리 코드
    private static final ArrayList<String> EXTENSIONS = new ArrayList<>(Arrays.asList(".mp3"));//mp3확장자 파일목록

    public SharedPreferences setting;

    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private Calendar mCalendar, currentCalendar;
    private int tmp_year, tmp_month, tmp_date;
    Date date = new Date();
    //시작 설정 클래스
    private TimePicker mTime;
    //예측시간
    //private ToggleButton toggleButton10,toggleButton30,toggleButton40,toggleButton60;
    private Button alarmDialog, repeatDialog, selectRing,vibeBtn;
    private Switch repeatSwitch, vibeSwitch, ringSwitch;

    private int selectedRepeatTime;//알람 주기
    private String selectedTime = null;//넘겨줄 알람주기
    private Uri tmpUri;//선택한 벨소리

    /*
     * 통지 관련 맴버 변수
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStop() {
        super.onStop();
        setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();

        // if autoLogin Checked, save values
        editor.putString("repeatTimeText", repeatDialog.getText().toString());//버튼 텍스트값 저장
        editor.putBoolean("repeatTimeBoolean", repeatSwitch.isChecked());//반복설정 스위치값저장
        editor.putBoolean("isVibe", vibeSwitch.isChecked());//진동설정 스위치값 저장
        editor.putBoolean("isRing", ringSwitch.isChecked());//진동설정 스위치값 저장
        editor.putInt("repeatTimeIdx", selectedRepeatTime);//라디오 인덱스값
        editor.putString("ringtoneUri",tmpUri.toString());//벨소리 uri
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tmpUri = Uri.parse(setting.getString("uriStr", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString())); // 기본 벨소리(알람)의 URI 디폴트값
        Log.i("onStart tmpUri : ", setting.getString("uriStr", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "DeepDreamerAlarm.db", null, 1);


        //알람 매니저를 취득
        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = Calendar.getInstance();
        currentCalendar = Calendar.getInstance();

        mCalendar.setTime(date);
        tmp_date = mCalendar.get(Calendar.DATE);
        tmp_month = mCalendar.get(Calendar.MONTH);
        tmp_year = mCalendar.get(Calendar.YEAR);

        //확인 버튼, 리셋버튼의 리스너를 등록
        setContentView(R.layout.activity_alarm_setting);
        Button b = (Button) findViewById(set);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                setAlarm();
                dbHelper.insert(mCalendar.get(mCalendar.MONTH) + 1, mCalendar.get(mCalendar.HOUR_OF_DAY), mCalendar.get(mCalendar.MINUTE));//addAlarm(int on, int day, int hour, int min, int vib, String ring)

                Log.i("년 : ", Integer.toString(mCalendar.get(mCalendar.YEAR)));
                Log.i("달 : ", Integer.toString(mCalendar.get(mCalendar.MONTH)));
                Log.i("분 : ", Integer.toString(mCalendar.get(mCalendar.MINUTE)));
                Log.i("result : ", dbHelper.getResult());
                //dbHelper.check();
            }
        });
        //취소버튼
        b = (Button) findViewById(R.id.reset);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(AlarmSettingActivity.this, "알람이 해제되었습니다.", Toast.LENGTH_LONG).show();
                resetAlarm();
                dbHelper.allDelete();
            }
        });
        //DATE PICKER DIALOG

        alarmDialog = (Button) findViewById(R.id.btn_alarm_dialog);
        initDate();//버튼 현재날짜로
        alarmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatePickerDialog dialog = new DatePickerDialog(AlarmSettingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        tmp_year = year;
                        tmp_month = month;
                        tmp_date = date;

                        String msg = String.format("%d 년 %d 월 %d 일", year, month + 1, date);
                        alarmDialog.setText(msg);//설정한 날짜로 텍스트 변경
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mCalendar.set(year, month, date, mTime.getHour(), mTime.getMinute());//설정한 날짜 저장

                        } else {
                            mCalendar.set(year, month, date, mTime.getCurrentHour(), mTime.getCurrentMinute());//설정한 날짜 저장
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
        mTime = (TimePicker) findViewById(R.id.time_picker);
        //버전에따라 다름
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            mTime.setHour(mCalendar.get(Calendar.HOUR_OF_DAY));
            mTime.setMinute(mCalendar.get(Calendar.MINUTE));
        } else {

            mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
            mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        }
        mTime.setOnTimeChangedListener(this);

        setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);//알람 설정 정보담을 setting

        repeatDialog = (Button) findViewById(R.id.bt_repeat);
        if (setting.getString("repeatTimeText", "") != null) {
            repeatDialog.setText(setting.getString("repeatTimeText", "반복 설정"));
        }
        selectedTime = setting.getString("repeatTime", "0분");//설정한 반복값 설정
        Log.i("Init selectedTime", selectedTime);
        repeatSwitch = (Switch) findViewById(R.id.sw_repeatAlarm);
        repeatSwitch.setChecked(setting.getBoolean("repeatTimeBoolean", false));
        //초기 버튼 활성화 유무
        if (repeatSwitch.isChecked()) repeatDialog.setEnabled(true);
        else repeatDialog.setEnabled(false);

        repeatDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] repeatTime = new String[]{"5분", "10분", "15분", "30분"};
                int initIdx = setting.getInt("repeatTimeIdx", 0);
                Log.i("initIDx : ", Integer.toString(initIdx));
                AlertDialog.Builder dialog = new AlertDialog.Builder(AlarmSettingActivity.this);
                dialog.setTitle("간격")
                        .setSingleChoiceItems(repeatTime, initIdx, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //선택한 아이템

                                selectedRepeatTime = i;
                            }
                        }).setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        repeatDialog.setText("다시 울림(" + repeatTime[selectedRepeatTime] + ")");

                        SharedPreferences.Editor editor = setting.edit();
                        editor.putString("repeatTime", repeatTime[selectedRepeatTime]);//시간설정한 값 저장
                        editor.commit();

                        selectedTime = repeatTime[selectedRepeatTime];
                        Log.i("selectedTime : ", selectedTime);
                    }
                }).setCancelable(false)
                        .show();
            }
        });

        repeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //스위치 체크되어있을때
                    repeatDialog.setEnabled(true);
                } else {
                    //스위치 해제
                    repeatDialog.setEnabled(false);
                    selectedRepeatTime = 0;
                }
            }
        });
        //초기 버튼 활성화 유무
        if (vibeSwitch.isChecked()) vibeBtn.setEnabled(true);
        else vibeBtn.setEnabled(false);
        vibeSwitch = (Switch) findViewById(R.id.sw_vibe);
        vibeSwitch.setChecked(setting.getBoolean("isVibe", false));//체크설정
        vibeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //스위치 체크되어있을때
                } else {
                    //스위치 해제
                }
            }
        });
        selectRing = (Button) findViewById(R.id.bt_ring);
        selectRing.setText(setting.getString("selectRingText", "벨소리"));
        //if(tmpUri==null)tmpUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        selectRing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER); // 암시적 Intent

                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알람음");  // 제목을 넣는다.

                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);  // 무음을 선택 리스트에서 제외

                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true); // 기본 벨소리는 선택 리스트에 넣는다.

                //intent.putExtra(" mp3 ", extensionFilter(Environment.getExternalStorageDirectory())); //mp3확장자도 벨소리 추가

                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM); //알람타입으로 설정


                startActivityForResult(intent, REQUEST_CODE_RINGTONE); // 벨소리 선택 창을 안드로이드 OS에 요청

            }
        });
        ringSwitch = (Switch) findViewById(R.id.sw_ring);
        ringSwitch.setChecked(setting.getBoolean("isRing", false));//체크설정
        //초기 버튼 활성화 유무
        if (ringSwitch.isChecked()) selectRing.setEnabled(true);
        else selectRing.setEnabled(false);
        ringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //스위치 체크되어있을때
                    selectRing.setEnabled(true);
                } else {
                    //스위치 해제
                    selectRing.setEnabled(false);
                }
            }
        });
    }

    //알람의 설정
    private void setAlarm() {
        currentCalendar = Calendar.getInstance();
        if (mCalendar.compareTo(currentCalendar) <= 0) {
            Toast.makeText(AlarmSettingActivity.this, "현재시간보다 전입니다. 알람시간을 다시 설정하세요.", Toast.LENGTH_LONG).show();
        } else {

            if (repeatSwitch.isChecked()) {//반복 주기 설정 했을때
                try {
                    int t = Integer.parseInt(selectedTime.substring(0, selectedTime.length() - 1));//반복 주기
                    Log.i("자른거 : ", selectedTime.substring(0, selectedTime.length() - 1));
                    //mManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), 1000*60*t,pendingIntent());//반복주기 설정
                    mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent());//알람 설정
                    //Toast.makeText(AlarmSettingActivity.this, t + "분 반복 알람", Toast.LENGTH_LONG).show();
                } catch (NullPointerException e) {
                    Toast.makeText(AlarmSettingActivity.this, "반복알람 시간을 체크하세요", Toast.LENGTH_LONG).show();
                }
                //setInexactRepeating(AlarmManager.RTC, tomorrow.getTime(), 24 * 60 * 60 * 1000, sender); 24시간 마다 울림

            }else{
                mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent());//알람 설정
            }

            int date = mCalendar.get(mCalendar.DATE) - currentCalendar.get(Calendar.DATE);
            int hour = mCalendar.get(mCalendar.HOUR) - currentCalendar.get(Calendar.HOUR);
            int minute = mCalendar.get(mCalendar.MINUTE) - currentCalendar.get(Calendar.MINUTE);

            if (minute < 0) {
                minute = 60 + minute;
                hour = hour - 1;
            }
            if (hour < 0) {
                hour = 24 + hour;
                date = date - 1;
            }

            if (date == 0) {
                if (hour == 0) {
                    Toast.makeText(AlarmSettingActivity.this, minute + "분 후 알람이 울립니다.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AlarmSettingActivity.this, date + "시간" + minute + "분 후 알람이 울립니다.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AlarmSettingActivity.this, date + "일" + hour + "시간" + minute + "분 후 알람이 울립니다.", Toast.LENGTH_LONG).show();
            }

            Log.i("알람이 세팅되었습니다", mCalendar.getTime().toString());


            Intent intent = new Intent(AlarmSettingActivity.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("alarmTime", String.format("%d 시 %d 분", mCalendar.get(mCalendar.HOUR), mCalendar.get(mCalendar.MINUTE)));
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }

    }

    //알람의 해제
    private void resetAlarm() {
        mManager.cancel(pendingIntent());
        Intent intent = new Intent(AlarmSettingActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("alarmTime", "알람 설정");//알람 해제 했을때 다시 알람 설정으로
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();

        Log.i("알람이 해제되었습니다", mCalendar.getTime().toString());
    }

    //알람의 설정 시각에 발생하는 인텐트 작성
    private PendingIntent pendingIntent() {
        Intent i = new Intent(AlarmSettingActivity.this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        return pi;
    }

    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(tmp_year, tmp_month, tmp_date, hourOfDay, minute);
        Log.i("시간이 바뀌었습니다", mCalendar.getTime().toString());
    }

    private void initDate() {
        //현재날짜로 설정
        final String initMsg = String.format("%d 년 %d 월 %d 일", tmp_year, tmp_month + 1, tmp_date);
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
        Log.i("initMsg : ", initMsg);
        Log.i("처음 시간 설정값", mCalendar.getTime().toString());
        Log.i("intiMsg : ", initMsg);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {

            case REQUEST_CODE_RINGTONE:

                if (resultCode == RESULT_OK) {

                    // 선택한 Ringtone(벨소리)를 받아온다.

                    Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    tmpUri = uri;
                    Log.i("onActivity tmpUri: ", tmpUri.toString());
                    // 벨소리 이름 얻는 방법

                    String ringToneName = RingtoneManager.getRingtone(this, uri).getTitle(this);
                    selectRing.setText("벨소리(" + ringToneName + ")");


                    SharedPreferences.Editor editor = setting.edit();
                    editor.putString("uriStr", uri.toString());
                    editor.putString("selectRingText", selectRing.getText().toString());//시간설정한 값 저장
                    editor.commit();


                }

        }

    }

    private ArrayList<String> extensionFilter(File folder) {
        ArrayList<String> result = new ArrayList<>();

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(extensionFilter(file));
                } else {
                    if (EXTENSIONS.contains(file.getName().substring(file.getName().lastIndexOf(".")))) {
                        result.add(file.toString());
                    }
                }
            }
        }

        return result;

    }
}