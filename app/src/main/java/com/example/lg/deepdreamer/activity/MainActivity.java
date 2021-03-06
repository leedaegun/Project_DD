package com.example.lg.deepdreamer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lg.deepdreamer.R;
import com.example.lg.deepdreamer.fragment.First_AlarmFragment;
import com.example.lg.deepdreamer.fragment.MainFragment;
import com.example.lg.deepdreamer.fragment.Second_MeasurementFragment;
import com.example.lg.deepdreamer.fragment.Third_SettingFragment;

//메인화면
public class MainActivity extends AppCompatActivity {


    private BackPressCloseHandler backPressCloseHandler;//뒤로 두번 핸들러


    private ViewPager vp;
    private PagerAdapter pagerAdapter;
    private LinearLayout ll;
    private ImageButton tab_default,tab_first,tab_second,tab_third;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);
        if(!NetworkConnection()){//네트워크 상태확인
            NotConnected_showAlert();
        }

        vp = (ViewPager)findViewById(R.id.vp);
        vp.setOffscreenPageLimit(3);//페이지가 4개니까 미리 3개페이지 준비
        ll = (LinearLayout)findViewById(R.id.ll);

        //탭 초기화

        tab_default = (ImageButton)findViewById(R.id.tab_default) ;//첫번째 탭
        tab_first = (ImageButton) findViewById(R.id.tab_first);//두번째 탭
        tab_second = (ImageButton)findViewById(R.id.tab_second);//세번째 탭
        tab_third = (ImageButton)findViewById(R.id.tab_third);//네번째 탭

        pagerAdapter = new pagerAdapter(getSupportFragmentManager());
        vp.setAdapter(pagerAdapter);
        vp.setCurrentItem(0);

        tab_default.setOnClickListener(movePageListener);
        tab_default.setTag(0);
        tab_first.setOnClickListener(movePageListener);
        tab_first.setTag(1);
        tab_second.setOnClickListener(movePageListener);
        tab_second.setTag(2);
        tab_third.setOnClickListener(movePageListener);
        tab_third.setTag(3);


        tab_default.setSelected(true);//초기화면 메인으로 설정

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                int i = 0;
                while(i<4)
                {
                    if(position==i)
                    {
                        ll.findViewWithTag(i).setSelected(true);
                    }
                    else
                    {
                        ll.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        backPressCloseHandler = new BackPressCloseHandler(this);


    }


    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int) v.getTag();

            int i = 0;
            while(i<4)
            {
                if(tag==i)
                {
                    ll.findViewWithTag(i).setSelected(true);
                }
                else
                {
                    ll.findViewWithTag(i).setSelected(false);
                }
                i++;
            }

            vp.setCurrentItem(tag);
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
                    return new MainFragment();
                case 1:
                    return new First_AlarmFragment();
                case 2:
                    return new Second_MeasurementFragment();
                case 3:
                    return new Third_SettingFragment();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 4;
        }


    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();

    }

    public class BackPressCloseHandler {
        private long backKeyPressedTime = 0;
        private Toast toast;
        private Activity activity;
        public BackPressCloseHandler(Activity context) { this.activity = context; }
        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return; }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel(); }
        }
        public void showGuide() {
            toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT); toast.show(); }
    }

    @Override
    public void onResume(){
        super.onResume();
        pagerAdapter.notifyDataSetChanged();

    }
    private void NotConnected_showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("네트워크 연결 오류");
        builder.setMessage("사용 가능한 무선네트워크가 없습니다.\n" + "먼저 무선네트워크 연결상태를 확인해 주세요.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // exit
                        //application 프로세스를 강제 종료
                        android.os.Process.killProcess(android.os.Process.myPid() );
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
    private boolean NetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
