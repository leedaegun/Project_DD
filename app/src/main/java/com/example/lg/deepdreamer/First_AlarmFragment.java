package com.example.lg.deepdreamer;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class First_AlarmFragment extends Fragment {
    private TextView tv_roll, tv_pitch;
    private Button bt_to_Alarm_Setting, bt_start, bt_Gyro;

    //MediaPlayer mPlayer = null;
    MediaRecorder mRecorder = null;
    String mPath = null;
    boolean isRecording = false;

    //Using the Accelometer & Gyroscoper 자이로센서 변수

    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager = null;

    //Using the Gyroscope
    private SensorEventListener mGyroLis;
    private Sensor mGgyroSensor = null;

    //Roll and Pitch
    private double pitch;
    private double roll;
    private double yaw;

    //timestamp and dt
    private double timestamp;
    private double dt;

    // for radian -> dgree
    private double RAD2DGR = 180 / Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;

    private boolean running=false;

    public First_AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_first__alarm, container, false);
        tv_roll = layout.findViewById(R.id.tv_roll);
        tv_pitch = layout.findViewById(R.id.tv_pitch);
        //알람설정버튼
        bt_to_Alarm_Setting = layout.findViewById(R.id.bt_to_Alarm_Setting);
        bt_to_Alarm_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmSettingActivity.class);
                startActivity(intent);

            }
        });
        //측정시작버튼
        bt_start = layout.findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 실행 중일 때 -> 중지 */
                if (isRecording == true) {

                    mRecorder.stop();
                    isRecording = false;
                    bt_start.setText("측정시작");
                    Toast.makeText(getActivity(), "측정을 종료합니다.", Toast.LENGTH_SHORT).show();

                }

                /* 실행 중이지 않을 때 -> 실행 */
                else {

                    initAudioRecorder();
                    mRecorder.start();

                    isRecording = true;
                    bt_start.setText("측정종료");
                    Toast.makeText(getActivity(), "측정을 시작합니다.", Toast.LENGTH_SHORT).show();

                }
            }
        });
        //Using the Gyroscope & Accelometer
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        //Using the Accelometer
        mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGyroLis = new GyroscopeListener();

        //Touch Listener for Accelometer
        bt_Gyro = layout.findViewById(R.id.bt_Gyro);
        bt_Gyro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* 실행 중이지 않을 때 -> 실행 */
                if(!running){
                    running = true;
                    bt_Gyro.setText("자이로측정종료");
                    Toast.makeText(getActivity(), "측정을 시작합니다.", Toast.LENGTH_SHORT).show();
                    mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_UI);


                }

                /* 실행 중일 때 -> 중지 */
                else
                {
                    running = false;
                    bt_Gyro.setText("자이로측정시작");
                    Toast.makeText(getActivity(), "측정을 종료합니다.", Toast.LENGTH_SHORT).show();
                    mSensorManager.unregisterListener(mGyroLis);

                }
            }
        });



        return layout;
    }

    void initAudioRecorder() {
        if (mRecorder != null) { // recorder에 뭐가 들어있으면 초기화해줌
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "Deep Dreamer/record.mp4";
        Log.d(TAG, "file path is " + mPath);
        mRecorder.setOutputFile(mPath);
        try {
            mRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.e("LOG", "onPause()");
        mSensorManager.unregisterListener(mGyroLis);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        mSensorManager.unregisterListener(mGyroLis);
    }

    private class GyroscopeListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            /* 각 축의 각속도 성분을 받는다. */
            double gyroX = event.values[0];
            double gyroY = event.values[1];
            double gyroZ = event.values[2];

            /* 각속도를 적분하여 회전각을 추출하기 위해 적분 간격(dt)을 구한다.
             * dt : 센서가 현재 상태를 감지하는 시간 간격
             * NS2S : nano second -> second */
            dt = (event.timestamp - timestamp) * NS2S;
            timestamp = event.timestamp;

            /* 맨 센서 인식을 활성화 하여 처음 timestamp가 0일때는 dt값이 올바르지 않으므로 넘어간다. */
            if (dt - timestamp*NS2S != 0) {

                /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
                 * 여기까지의 pitch, roll의 단위는 '라디안'이다.
                 * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */
                pitch = pitch + gyroY*dt;
                roll = roll + gyroX*dt;
                yaw = yaw + gyroZ*dt;

                tv_roll.setText("roll : "+roll);
                tv_pitch.setText("pitch : "+pitch);

                Log.e("LOG", "GYROSCOPE           [X]:" + String.format("%.4f", event.values[0])
                        + "           [Y]:" + String.format("%.4f", event.values[1])
                        + "           [Z]:" + String.format("%.4f", event.values[2])
                        + "           [Pitch]: " + String.format("%.1f", pitch*RAD2DGR)
                        + "           [Roll]: " + String.format("%.1f", roll*RAD2DGR)
                        + "           [Yaw]: " + String.format("%.1f", yaw*RAD2DGR)
                        + "           [dt]: " + String.format("%.4f", dt));

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }



}



