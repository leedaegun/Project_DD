package com.example.lg.deepdreamer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class First_AlarmFragment extends Fragment {
    private TextView tv_roll, tv_pitch;
    private Button bt_to_Alarm_Setting, bt_start,bt_service;
    String getAlarmTime=null;
    //MediaPlayer mPlayer = null;
    //녹음을 위한 변수
    //MediaRecorder mRecorder = null;
    //private String mPath,tmpPath = null;
    boolean isRecording = false;
    boolean isService = false;
    private AutoVoiceReconizer autoVoiceRecorder;//녹음 클래스 선언
    private TextView statusTextView;

    //파일 저장 인덱스 -> 저장날짜 private  int tmp_year,tmp_month,tmp_date,tmp_hour,tmp_mintue;

    //Using the Accelometer & Gyroscoper 자이로센서 변수

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

    //서비스 변수

    Intent mService;

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
                startActivityForResult(intent,101);


            }
        });

        //녹음 초기화
        autoVoiceRecorder = new AutoVoiceReconizer( handler );
        statusTextView = layout.findViewById( R.id.text_view_status );
        statusTextView.setText("준비..");

        //자이로 센서 초기화 Using the Gyroscope & Accelometer
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        //Using the Accelometer
        mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGyroLis = new GyroscopeListener();

        //측정시작버튼
        bt_start = layout.findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 실행 중일 때 -> 중지 */
                if (isRecording) {

                    //mRecorder.stop();
                    autoVoiceRecorder.stopLevelCheck();//녹음 종료
                    mSensorManager.unregisterListener(mGyroLis);//자이로 측정종료
                    //fileTransport ft = new fileTransport(); //녹음 종료와 동시에 서버로 파일전송
                    //ft.execute(tmpPath);

                    isRecording = false;
                    bt_start.setText("측정시작");
                    Toast.makeText(getActivity(), "측정을 종료합니다.", Toast.LENGTH_SHORT).show();

                }

                /* 실행 중이지 않을 때 -> 실행 */
                else {

                    //initAudioRecorder();
                    //mRecorder.start();
                    autoVoiceRecorder.startLevelCheck();//녹음 측정시작
                    mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_UI);//자이로측정시작

                    isRecording = true;
                    bt_start.setText("측정종료");
                    Toast.makeText(getActivity(), "측정을 시작합니다.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        bt_service=layout.findViewById(R.id.bt_service);
        bt_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isService) {
                    isService = false;
                    bt_service.setText("서비스 시작");

                    mService = new Intent(getActivity(),GyroRecordService.class);
                    getActivity().stopService(mService);



                }

                /* 실행 중이지 않을 때 -> 실행 */
                else {
                    isService = true;
                    bt_service.setText("서비스 종료");

                    mService = new Intent(getActivity(),GyroRecordService.class);
                    getActivity().startService(mService);


                }
            }
        });





        return layout;
    }
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch( msg.what ){
                case AutoVoiceReconizer.VOICE_READY:
                    statusTextView.setTextColor( Color.BLACK );
                    statusTextView.setText("준비...");
                    break;
                case AutoVoiceReconizer.VOICE_RECONIZING:
                    statusTextView.setTextColor( Color.BLACK );
                    statusTextView.setText("목소리 인식중...");
                    break;
                case AutoVoiceReconizer.VOICE_RECONIZED :
                    statusTextView.setTextColor( Color.BLACK );
                    statusTextView.setText("목소리 감지... 녹음중...");
                    break;
                case AutoVoiceReconizer.VOICE_RECORDING_FINSHED:
                    statusTextView.setTextColor( Color.BLACK );
                    statusTextView.setText("목소리 녹음 완료 재생 버튼을 누르세요...");
                    break;
/*
                case AutoVoiceReconizer.VOICE_PLAYING:
                    statusTextView.setTextColor( Color.BLACK );
                    statusTextView.setText("플레이중...");
                    break;
*/
                case AutoVoiceReconizer.FILE_PATH:
                    statusTextView.setTextColor( Color.BLACK );
                    statusTextView.setText("서버전송중...");
                    Log.e("파일경로ㅗ로롤로로롤ㄹㄹㄹ : ",String.valueOf(msg.obj));
                    fileTransport ft = new fileTransport(); //녹음 종료와 동시에 서버로 파일전송
                    ft.execute(String.valueOf(msg.obj));
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                //getAlarmTime = getArguments().getString("alarmTime");
                //if(getAlarmTime!=null)
                Bundle bundle = data.getExtras();
                getAlarmTime = bundle.getString("alarmTime");
                bt_to_Alarm_Setting.setText(getAlarmTime);
            }
        }
    }

    /*
        void initAudioRecorder() {
            if (mRecorder != null) { // recorder에 뭐가 들어있으면 초기화해줌
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//output file 에 audio track 이 포함되는과정
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//이거 설정해제해보기 저장되나안되나
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new RecorderTask(mRecorder), 0, 500);

            mCalendar = Calendar.getInstance();

            //파일 구분을 위한 파일이 저장된 날짜
            tmp_year=mCalendar.get(Calendar.YEAR);
            tmp_month=mCalendar.get(Calendar.MONTH)+1;
            tmp_date=mCalendar.get(Calendar.DATE);

            tmp_hour = mCalendar.get(Calendar.HOUR);
            tmp_mintue = mCalendar.get(Calendar.MINUTE);




            mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + tmp_year + "/" + tmp_month+ "/" + tmp_date + "/"+tmp_hour+":" + tmp_mintue+".record.mp4";
            tmpPath = mPath;
            Log.e("tmpPath " , tmpPath);
            Log.d("file path is " , mPath);
            mRecorder.setOutputFile(mPath);
            try {
                mRecorder.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    */
    @Override
    public void onResume() {
        super.onResume();/*

*/

    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e("LOG", "onPause()");
        mSensorManager.unregisterListener(mGyroLis);
        /* if (mMusicPlayerService != null) {
            if (!mMusicPlayerService.isPlaying()) {
                // 음악이 정지 중일 경우는 서비스를 계속 실행할 필요가 없으므로 정지한다.
                mMusicPlayerService.stopSelf();
            }
            unbindService(mMusicPlayerServiceConnection);
            mMusicPlayerService = null;
        }
*/
    }

    @Override
    public void onStop() {
        super.onStop();


        mSensorManager.unregisterListener(mGyroLis);

    }




/*
    private class RecorderTask extends TimerTask {

        private MediaRecorder mRecorder;

        public RecorderTask(MediaRecorder mRecorder) {
            this.mRecorder = mRecorder;
        }

        public void run(){
           getActivity().runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   int amplitude = mRecorder.getMaxAmplitude();
                   double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude) / 32768);//로그안에 들어있는값 제곱해보기
                   test.setText("" + amplitudeDb);
               }
           });
       }

    }*/

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
/*
                Log.e("LOG", "GYROSCOPE           [X]:" + String.format("%.4f", event.values[0])
                        + "           [Y]:" + String.format("%.4f", event.values[1])
                        + "           [Z]:" + String.format("%.4f", event.values[2])
                        + "           [Pitch]: " + String.format("%.1f", pitch*RAD2DGR)
                        + "           [Roll]: " + String.format("%.1f", roll*RAD2DGR)
                        + "           [Yaw]: " + String.format("%.1f", yaw*RAD2DGR)
                        + "           [dt]: " + String.format("%.4f", dt));
*/
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public class fileTransport extends AsyncTask<String, Integer, Void> {

        FileInputStream fileInputStream = null;


        @Override
        protected Void doInBackground(String... params) {

            String iFileName = params[0];
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String Tag="fSnd";

            //인풋확인 로그캣
            Log.e("파리미터----------->:",params[0]);
            File sourceFile = new File(params[0]);
            try {
                fileInputStream = new FileInputStream(sourceFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
            /* 서버연결 */

                URL url = new URL("http://192.168.0.89/transport.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // Allow Inputs
                conn.setDoInput(true);
                // Allow Outputs
                conn.setDoOutput(true);
                // Don't use a cached copy.
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("uploaded_file", iFileName);



                /* 안드로이드 -> 서버 파라메터값 전달 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + iFileName +"\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag,"Headers are written");

                // create a buffer of maximum size
                int bytesAvailable = fileInputStream.available();

                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[ ] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0,bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                fileInputStream.close();

                dos.flush();

                //리스폰 코드 로그캣
                Log.e(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));

                InputStream is = conn.getInputStream();

                // retrieve the response from server
                int ch;

                StringBuffer b =new StringBuffer();
                while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
                String s=b.toString();

                //로그캣
                Log.i("Response",s);

                dos.close();




            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(getActivity(), "파일전송 성공!!.", Toast.LENGTH_SHORT).show();


        }

    }





}



