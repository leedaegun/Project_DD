package com.example.lg.deepdreamer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
        */
public class Third_SettingFragment extends Fragment {

    MediaPlayer mPlayer;
    String mPath = null;
    boolean isPlaying = false;
    private Button bt_Logout,bt_goto_detail_setting,bt_play;
    TextView tv;
    public SharedPreferences setting;

    public Third_SettingFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_third__setting, container, false);

        bt_Logout=layout.findViewById(R.id.bt_Logout);
        bt_goto_detail_setting=layout.findViewById(R.id.bt_goto_detail_setting);
        bt_play = layout.findViewById(R.id.bt_play);
        tv=layout.findViewById(R.id.tv);

        bt_goto_detail_setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),DetailSettingActivity.class);
                startActivity(intent);
            }
        });

        bt_Logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new AlertDialog.Builder(getActivity())
                        .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .show();
            }
        });

        bt_play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (isPlaying == false) {
                    try {
                        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.mp4";
                        mPlayer.setDataSource(mPath);
                        mPlayer.prepare();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPlayer.start();

                    isPlaying = true;
                    bt_play.setText("그만듣기");
                }
                else {
                    mPlayer.stop();

                    isPlaying = false;
                    bt_play.setText("녹음듣기");
                }
            }
        });
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                bt_play.setText("녹음듣기");
            }
        });



        //tv.setText(setting.getString("id",""));


        return layout;
    }

}
