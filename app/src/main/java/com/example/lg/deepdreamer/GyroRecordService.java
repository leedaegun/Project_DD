package com.example.lg.deepdreamer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

//자이로 센서,녹음 측정 클래스
public  class GyroRecordService extends Service implements SensorEventListener {

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

    public GyroRecordService() {
    }

    @Override
    public void onCreate() {
        Log.e("LOG", "onCreate()");
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //Using the Accelometer
        mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
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
        timestamp = event.timestamp;

            /* 맨 센서 인식을 활성화 하여 처음 timestamp가 0일때는 dt값이 올바르지 않으므로 넘어간다. */
        if (dt - timestamp*NS2S != 0) {

                /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
                 * 여기까지의 pitch, roll의 단위는 '라디안'이다.
                 * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */
            pitch = pitch + gyroY*dt;
            roll = roll + gyroX*dt;
            yaw = yaw + gyroZ*dt;

            //tv_roll.setText("roll : "+roll);
            //tv_pitch.setText("pitch : "+pitch);

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

            Intent intent = new Intent(GyroRecordService.this, RealMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(GyroRecordService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());//빌더 선언
            builder.setSmallIcon(R.drawable.ic_action_name)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("서비스")
                    .setContentText("서비스 동작 중 입니다.")
                    //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            Bitmap largeIcon = BitmapFactory.decodeResource(Resources.getSystem(),R.mipmap.ic_launcher);
            builder.setLargeIcon(largeIcon);//큰 아이콘
            builder.setColor(Color.RED);//색깔
            notificationmanager.notify(1, builder.build());




        };

    };

}
