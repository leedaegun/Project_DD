package com.example.lg.deepdreamer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
        */

//설정 프래크먼트
//로그아웃, 녹음 듣기 세부설정 이동
public class Third_SettingFragment extends Fragment {

    private Button bt_Logout,bt_goto_detail_setting,bt_play;
    ListView listView;
    private AutoVoiceReconizer autoVoiceRecorder;//녹음 클래스 선언

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

            }
        });

        listView = layout.findViewById(R.id.lv_record);
        //파일목록 로딩
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/DeepDreamer/";
        File list = new File(path);
        if(!list.isDirectory()){
            if(!list.mkdirs()){}
        }
        File[] files = list.listFiles();
        List<String> fileNameList = new ArrayList<>();
        for(int i=0;i<files.length;i++){
            fileNameList.add(files[i].getName());
        }

        autoVoiceRecorder = new AutoVoiceReconizer(handler );//재생클래스 선언
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,fileNameList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // get TextView's Text.
                //File playFile = (File) parent.getItemAtPosition(position) ;//클릭한거 파일화
                String playFile = (String) parent.getItemAtPosition(position) ;
                Toast.makeText(getActivity(),"파일 재생..", Toast.LENGTH_SHORT).show();
                Log.i("클릭한 파일 이름",playFile);
                autoVoiceRecorder.playVoice(playFile);//파일재생
                // TODO : use strText
            }
        }) ;







        return layout;
    }

    //녹음재생 핸들러
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch( msg.what ){
                case AutoVoiceReconizer.VOICE_PLAYING:
                    Toast.makeText(getActivity(),"파일 재생..", Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    };


}
