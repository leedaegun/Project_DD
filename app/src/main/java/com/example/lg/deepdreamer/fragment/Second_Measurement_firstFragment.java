package com.example.lg.deepdreamer.fragment;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;

import com.example.lg.deepdreamer.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Second_Measurement_firstFragment extends Fragment {


    final static String fileRoot=Environment.getExternalStorageDirectory().getAbsolutePath()+"/DeepDreamer/gyroData/";
    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private Calendar mCalendar, currentCalendar;
    private int tmp_year, tmp_month, tmp_date,tmp_gyro_date=16;
    private Button alarmDialog;
    private Date date = new Date();
    private Calendar maxDate,minDate ;
    private BufferedReader bufferedReader;
    private LineChart  lineChart;
    private Integer[] gyroData;
    //BarChart barChart;
    public Second_Measurement_firstFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_second__measurement_first, container, false);
        mManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득

        mCalendar = Calendar.getInstance();
        currentCalendar = Calendar.getInstance();//현재날짜
        maxDate = Calendar.getInstance();//최대 날짜
        minDate = Calendar.getInstance();//최소날짜

        mCalendar.setTime(date);
        tmp_date = mCalendar.get(Calendar.DATE);
        tmp_month = mCalendar.get(Calendar.MONTH);
        tmp_year = mCalendar.get(Calendar.YEAR);

        maxDate.set(2018, 11 - 1, 23);//2018.11.23 이 멕스날짜
        minDate.set(2018, 11 - 1, 14);//20181114부터
        alarmDialog = layout.findViewById(R.id.btn_cal_dialog);
        initDate();//버튼 현재날짜로
        alarmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        tmp_year = year;
                        tmp_month = month;
                        tmp_date = date;
                        tmp_gyro_date =date;
                        String msg = String.format("%d 년 %d 월 %d 일", year, month + 1, date);
                        alarmDialog.setText(msg);//설정한 날짜로 텍스트 변경
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mCalendar.set(year, month, date);//설정한 날짜 저장

                        } else {
                            mCalendar.set(year, month, date);//설정한 날짜 저장
                        }

                        Log.i("날짜가 바뀌었습니다", mCalendar.getTime().toString());
                        //Toast.makeText(AlarmSettingActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE));//오늘날짜로 기본 설정

                currentCalendar.setTime(date);
                dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());//최소
                dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());//최대
                dialog.show();
                //((MainActivity) getActivity()).refresh();//화면갱신
                refresh();

            }

        });
        lineChart = layout.findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<>();//꺽은선 그래프


        try{

            //file = new File(fileRoot+tmp_date+".txt");
            InputStream is = new FileInputStream(fileRoot+tmp_gyro_date+".txt");
           // Log.e("Root : ",fileRoot+tmp_date);
            Log.e("gyto Root : ",fileRoot+tmp_gyro_date);
            gyroData = new Integer[1024];
            //fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            int line;
            int time=0;
            while((line = bufferedReader.read())!=-1){
                if((char)line ==' ')continue;
                if(time>=1024){
                        break;
                }
                else{

                    gyroData[time++]=line;

                }
            }
            is.close();
            bufferedReader.close();

           for(int i=0;i<256;i++){
                entries.add(new Entry(i, gyroData[i]));
                //Log.e("gryoData",gyroData[i].toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

/*
        entries.add(new Entry(1f, 8));
        entries.add(new Entry(2f, 6));
        entries.add(new Entry(3f, 2));
        entries.add(new Entry(4f, 18));
        entries.add(new Entry(5f, 9));
        entries.add(new Entry(6f, 16));
        entries.add(new Entry(7f, 5));
        entries.add(new Entry(8f, 3));
        entries.add(new Entry(10f, 7));
        entries.add(new Entry(11f, 9));
*/
        LineDataSet dataset = new LineDataSet(entries, "수면 데이터");

        List<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        labels.add("August");
        labels.add("September");
        labels.add("October");
        labels.add("November");
        labels.add("December");

        LineData data = new LineData(dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //그래프 색깔
        dataset.setDrawCircles(true); //선 둥글게 만들기
        //dataset.setDrawFilled(true); //그래프 밑부분 색칠

        lineChart.setData(data);
        lineChart.animateY(5000);
        return layout;
    }
    private void initDate() {
        //현재날짜로 설정
        final String initMsg = String.format("%d 년 %d 월 %d 일", tmp_year, tmp_month + 1, tmp_date);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // runOnUiThread를 추가하고 그 안에 UI작업을 한다.
                getActivity().runOnUiThread(new Runnable() {
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
    public void refresh(){

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }
}
