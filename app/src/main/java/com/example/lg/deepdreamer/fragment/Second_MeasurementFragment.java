package com.example.lg.deepdreamer.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lg.deepdreamer.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Second_MeasurementFragment extends Fragment {
    ViewPager inner_vp;
    LinearLayout inner_ll;
    public Second_MeasurementFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_second__measurement, container, false);

        inner_vp = layout.findViewById(R.id.inner_vp);
        inner_vp.setOffscreenPageLimit(2);//페이지가 3개니까 미리 2개페이지 준비
        inner_ll = layout.findViewById(R.id.inner_ll);

        TextView inner_tab_first = layout.findViewById(R.id.inner_tab_first);//첫번째 탭- 하루
        TextView inner_tab_second = layout.findViewById(R.id.inner_tab_second);//두번째 탭- 일주일
        //TextView inner_tab_third = layout.findViewById(R.id.inner_tab_third);//세번째 탭-한달

        inner_vp.setAdapter(new pagerAdapter(getChildFragmentManager()));
        inner_vp.setCurrentItem(0);

        inner_tab_first.setOnClickListener(movePageListener);
        inner_tab_first.setTag(0);
        inner_tab_second.setOnClickListener(movePageListener);
        inner_tab_second.setTag(1);
        //inner_tab_third.setOnClickListener(movePageListener);
        //inner_tab_third.setTag(2);

        inner_tab_first.setSelected(true);

        inner_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                int i = 0;
                while(i<2)
                {
                    if(position==i)
                    {
                        inner_ll.findViewWithTag(i).setSelected(true);
                    }
                    else
                    {
                        inner_ll.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });


        return layout;


    }

    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int) v.getTag();

            int i = 0;
            while(i<2)
            {
                if(tag==i)
                {
                    inner_ll.findViewWithTag(i).setSelected(true);
                }
                else
                {
                    inner_ll.findViewWithTag(i).setSelected(false);
                }
                i++;
            }

            inner_vp.setCurrentItem(tag);
        }
    };
    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new Second_Measurement_firstFragment();
                case 1:
                    return new Second_Measurement_secondFragment();
                //case 2:
                //    return new Second_Measurement_thirdFragment();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 2;
        }


    }


}
