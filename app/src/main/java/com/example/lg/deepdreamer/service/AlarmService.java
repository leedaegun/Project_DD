package com.example.lg.deepdreamer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.example.lg.deepdreamer.util.WakeLockUtil;
import com.example.lg.deepdreamer.activity.AlarmPopUpActivity;

public class AlarmService extends Service {
    //알람 서비스
    private MediaPlayer mMediaPlayer;   // MediaPlayer 변수 선언
    private Vibrator mVibrator;
    private static final long[] sVibratePattern = new long[] { 500, 500 };   // 진동 패턴 정의(0.5초 진동, 0.5초 쉼)
    //진동 설정은 개발자가 직접 해야됨
    public AlarmService() {
    }
    @Override

    public void onCreate() {

        super.onCreate();


        //Power on

        WakeLockUtil.acquireCpuWakeLock(this);


    }
    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        // No intent, tell the system not to restart us.

        if (intent == null) {

            stopSelf();

            return START_NOT_STICKY;

        }
        /*
        startForeground(1,new Notification());

        //startForeground 를 사용하면 notification 을 보여주어야 하는데 없애기 위한 코드

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("")
                    .setContentText("")
                    .build();

        }else{
            notification = new Notification(0, "", System.currentTimeMillis());
            notification.setLatestEventInfo(getApplicationContext(), "", "", null);
        }

        nm.notify(startId, notification);
        nm.cancel(startId);*/
        Uri ringring = intent.getParcelableExtra("ringUri");//사용자가 선택한 벨소리

        if(intent.getBooleanExtra("isVibe",false)) playMusic(ringring);    // 음악 및 진동 시작
        if(intent.getBooleanExtra("isRing",false)) vibrator();

        Intent popUpIntent = new Intent(this,AlarmPopUpActivity.class);//알람 종료 화면
        startActivity(popUpIntent);

        Log.i("서비스에서 받은 ringUri : ",intent.getParcelableExtra("ringUri").toString());//
        Log.i("서비스에서 받은 isVibe : ",Boolean.toString(intent.getBooleanExtra("isVibe",false)));

        return  START_REDELIVER_INTENT;

    }



    @Override

    public void onDestroy() {

        super.onDestroy();

        stopMusic();   // 음악 및 진동 중지
        stopVibrator();  // 진동 중지
        // Power off
        WakeLockUtil.releaseCpuWakeLock();

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private void vibrator(){

        //진동 설정 했으면 진동진동
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            //api 26 이상
            mVibrator.vibrate(VibrationEffect.createWaveform(sVibratePattern,0));
        } else {
            //api 26 이하
            mVibrator.vibrate(sVibratePattern,0);// 진동 시작 (패턴으로 진동, '0':무한 반복, -1:반복 없음)
        }
    }
    private void playMusic(Uri uri) {

        stopMusic();  // 플레이 할 때 가장 먼저 음악 중지 실행
        mMediaPlayer = new MediaPlayer();  // 1. MediaPlayer 객체 생성
        try {

            mMediaPlayer.setDataSource(this, uri);  // 2. 데이터 소스 설정 (인터넷에 있는 음악 파일도 가능함)

            startAlarm(mMediaPlayer);

        } catch (Exception ex) {

            try {

                mMediaPlayer.reset();    // MediaPlayer의 Error 상태 초기화

                //setDataSourceFromResource(getResources(), mMediaPlayer, R.raw.fallbackring); // fallbackring.ogg 사용

                startAlarm(mMediaPlayer);

            } catch (Exception ex2) {

                ex2.printStackTrace();

            }


        }

    }
    private void startAlarm(MediaPlayer player) throws java.io.IOException, IllegalArgumentException, IllegalStateException {

        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {   // 현재 Alarm 볼륨 구함

            player.setAudioStreamType(AudioManager.STREAM_ALARM);    // Alarm 볼륨 설정

            player.setLooping(true);    // 음악 반복 재생

            player.prepare();   // 3. 재생 준비

            player.start();    // 4. 재생 시작

        }


    }



    public void stopMusic() {

        if (mMediaPlayer != null) {

            mMediaPlayer.stop();     // 5. 재생 중지

            mMediaPlayer.release();    // 6. MediaPlayer 리소스 해제

            mMediaPlayer = null;

        }

    }
    public  void stopVibrator(){
        if(mVibrator!=null){
            mVibrator.cancel();
        }

    }




}
