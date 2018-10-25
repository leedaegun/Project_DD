package com.example.lg.deepdreamer.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.lg.deepdreamer.server.FileTransport;
import com.example.lg.deepdreamer.util.AutoVoiceReconizer;
import com.example.lg.deepdreamer.util.ManagerNotification;
import com.example.lg.deepdreamer.util.ServiceThread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

//자이로 센서,녹음 측정 클래스
public  class GyroRecordService extends Service implements SensorEventListener{

    final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DeepDreamer";
    final static String filename = "logfile.txt";
    private boolean isStop;
    //알림 매니저
    private NotificationManager notificationmanager = null;
    ServiceThread thread;
    //파일 전송 클래스
    private FileTransport fileTransport;

    //녹음 클래스 선언
    private AutoVoiceReconizer autoVoiceRecorder;

    //Using the Accelometer & Gyroscoper 자이로센서 변수

    private SensorManager mSensorManager = null;

    //Using the Gyroscope

    private Sensor mGgyroSensor = null;

    private String transportData = "";
    //Roll and Pitch

    double oldValue = 0;
    double newValue = 1000;
    double diff;

    private double pitch;
    private double roll;
    private double yaw;

    //timestamp and dt
    private double timestamp;
    private double dt;

    // for radian -> dgree
    private double RAD2DGR = 180 / Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;

    public GyroRecordService() {
    }

    @Override
    public void onCreate() {
        Log.e("LOG", "onCreate()");
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //Using the Accelometer
        mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Thread counter = new Thread(new Counter());
        counter.start();

        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);//알람매니저 초기화

        thread = new ServiceThread(notifiHandler);//측정시 알림 있도록 헨들러 설정
        thread.start();//알림 쓰레드 시작

        autoVoiceRecorder = new AutoVoiceReconizer( handler );//녹음 클래스 객체
        autoVoiceRecorder.startLevelCheck();//녹음 측정시작

        if(mGgyroSensor != null)mSensorManager.registerListener(this, mGgyroSensor, SensorManager.SENSOR_DELAY_UI);

        Toast.makeText(this,"서비스 시작",Toast.LENGTH_SHORT).show();

        return Service.START_REDELIVER_INTENT;
        //return Service.START_STICKY;서비스 강제 종료시 다시 Service 재시작하지만 서비스자체를 null로 초기화 시켜 다시 시작
        //return Service.START_NOT_STICKY;   강제종료시 Service 재시작 하지 않음
        //return Service.START_REDELIVER_INTENT;  STICKY와 같지만 서비스자체를 null로 초기화 하지 않고 그대로 유지하며 다시 시작
        //측정값을 계속 유지해야하니깐 START_REDELIVER_INTENT 이거 사용하면 좋을듯 단 Activity에서 호출 할때 value 값을 전달하고 싶으면 flag 사용
    }

    @Override
    public void onDestroy() {
        Log.e("LOG", "onDestroy()");
        Toast.makeText(this,"서비스 종료",Toast.LENGTH_SHORT).show();
        thread.stopForever();//쓰레드 중지
        thread = null;//쓰레드 null값 넣어서 회수
        autoVoiceRecorder.stopLevelCheck();//녹음 측정시작
        if(mSensorManager !=null) mSensorManager.unregisterListener(this);
        isStop = true;
        WriteTextFile(foldername, filename, transportData);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("LOG", "onUnbind()");
        return super.onUnbind(intent);
    }
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
        //dt = (event.timestamp - timestamp);
        timestamp = event.timestamp;

            /* 맨 센서 인식을 활성화 하여 처음 timestamp가 0일때는 dt값이 올바르지 않으므로 넘어간다. */
        if (dt - timestamp*NS2S != 0) {

                /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
                 * 여기까지의 pitch, roll의 단위는 '라디안'이다.
                 * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */

            pitch = pitch + gyroY*dt;
            roll = roll + gyroX*dt;
            yaw = yaw + gyroZ*dt;

            oldValue = newValue;
            newValue = Math.sqrt((roll*roll + pitch*pitch + yaw*yaw) / 3);
            diff = Math.abs((newValue - oldValue) / oldValue);



            //tv_roll.setText("roll : "+roll);
            //tv_pitch.setText("pitch : "+pitch);
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
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch( msg.what ){
                case AutoVoiceReconizer.VOICE_READY:
                    //statusTextView.setText("준비...");
                    break;
                case AutoVoiceReconizer.VOICE_RECONIZING:
                    //statusTextView.setTextColor( Color.BLACK );
                    //statusTextView.setText("목소리 인식중...");
                    break;
                case AutoVoiceReconizer.VOICE_RECONIZED :
                    //statusTextView.setTextColor( Color.BLACK );
                    //statusTextView.setText("목소리 감지... 녹음중...");
                    break;
                case AutoVoiceReconizer.VOICE_RECORDING_FINSHED:
                    //statusTextView.setTextColor( Color.BLACK );
                    //statusTextView.setText("목소리 녹음 완료 재생 버튼을 누르세요...");
                    break;
/*
                case AutoVoiceReconizer.VOICE_PLAYING:
                    statusTextView.setTextColor( Color.WHITE );
                    statusTextView.setText("플레이중...");
                    break;
*/
                case AutoVoiceReconizer.FILE_PATH:
                    //statusTextView.setTextColor( Color.BLACK );
                    //statusTextView.setText("서버전송중...");
                    Log.e("파일경로ㅗ로롤로로롤ㄹㄹㄹ : ",String.valueOf(msg.obj));
                    fileTransport = new FileTransport(); //녹음 종료와 동시에 서버로 파일전송
                    fileTransport.execute(String.valueOf(msg.obj));
                    break;
            }
        }
    };
    private Handler notifiHandler = new Handler() {

        public void handleMessage(Message msg) {

            ManagerNotification.operatingService(getApplicationContext());



        };

    };

    //텍스트내용을 경로의 텍스트 파일에 쓰기
    public void WriteTextFile(String foldername, String filename, String contents){
        try{
            File dir = new File (foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private class Counter implements Runnable{
        private Handler handler = new Handler();

        @Override
        public void run() {
            while (!isStop){



                handler.post(new Runnable() { @Override public void run() {
                    transportData += Double.toString(diff*100f)+"\n";
                    // Log로 Count 찍어보기
                    Log.e("자이로", transportData+ ""); } });


            try {
                thread.sleep(1000);

                //Log.e("자이로 : ",transportData);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }
        }
    }

}
