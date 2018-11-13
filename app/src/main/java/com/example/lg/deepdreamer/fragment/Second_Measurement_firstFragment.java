package com.example.lg.deepdreamer.fragment;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Second_Measurement_firstFragment extends Fragment {



    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private Calendar mCalendar, currentCalendar;
    private int tmp_year, tmp_month, tmp_date;
    private Button alarmDialog;
    Date date = new Date();

    LineChart  lineChart;
    //BarChart barChart;
    public Second_Measurement_firstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout =  (RelativeLayout)inflater.inflate(R.layout.fragment_second__measurement_first, container, false);
        mManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = Calendar.getInstance();
        currentCalendar = Calendar.getInstance();
        mCalendar.setTime(date);
        tmp_date = mCalendar.get(Calendar.DATE);
        tmp_month = mCalendar.get(Calendar.MONTH);
        tmp_year = mCalendar.get(Calendar.YEAR);
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
                dialog.getDatePicker().setMinDate(currentCalendar.getTime().getTime());
                dialog.show();

            }
        });
        lineChart = layout.findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<>();//꺽은선 그래프
        //(x,y)좌표 입력
        for(int i=0;i<10;i++){
            entries.add(new Entry(i, i));
        }
        /*
        entries.add(new Entry(0f, 4));
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
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        dataset.setDrawCircles(true); //선 둥글게 만들기
        dataset.setDrawFilled(true); //그래프 밑부분 색칠

        lineChart.setData(data);
        lineChart.animateY(5000);
/*
        barChart = layout.findViewById(R.id.chart);

        List<BarEntry> entries = new ArrayList<BarEntry>();//바그래프

        //각 지점값 설정
        entries.add(new BarEntry(0f, 4));
        entries.add(new BarEntry(1f, 8));
        entries.add(new BarEntry(2f, 6));
        entries.add(new BarEntry(3f, 2));
        entries.add(new BarEntry(4f, 18));
        entries.add(new BarEntry(5f, 9));
        entries.add(new BarEntry(6f, 16));
        entries.add(new BarEntry(7f, 5));
        entries.add(new BarEntry(8f, 3));
        entries.add(new BarEntry(10f, 7));
        entries.add(new BarEntry(11f, 9));


        BarDataSet bardataset = new BarDataSet(entries,"# of Calls");

        ArrayList<String> labels = new ArrayList<String>();
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

        //데이터셋 지정
        BarData data = new BarData(bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS); //


        barChart.setData(data);
        barChart.invalidate();
        barChart.animateY(4000);//애니메이션 기능
        */

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
}
