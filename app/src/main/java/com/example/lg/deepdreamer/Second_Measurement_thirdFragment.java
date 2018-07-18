package com.example.lg.deepdreamer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Second_Measurement_thirdFragment extends Fragment {

    LineChart lineChart;

    public Second_Measurement_thirdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.fragment_second__measurement_third, container, false);

        lineChart = layout.findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<>();//꺽은선 그래프
        //(x,y)좌표 입력
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

        LineDataSet dataset = new LineDataSet(entries, "# of Calls");

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

        return layout;
    }

}
