package com.example.lg.deepdreamer.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.lg.deepdreamer.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Second_Measurement_firstFragment extends Fragment {

    LineChart  lineChart;
    BarChart barChart;
    public Second_Measurement_firstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout =  (RelativeLayout)inflater.inflate(R.layout.fragment_second__measurement_first, container, false);


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
        return layout;
    }

}
