package com.example.lg.deepdreamer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

public class AlarmService extends Service {

    private MediaPlayer mMediaPlayer;   // MediaPlayer 변수 선언
    private Vibrator mVibrator;
    private static final long[] sVibratePattern = new long[] { 500, 500 };   // 진동 패턴 정의(0.5초 진동, 0.5초 쉼)
    /*
    * mVibrator =  (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
mVibrator.vibrate(sVibratePattern, 0);   // 진동 시작 (패턴으로 진동, '0':무한 반복, -1:반복 없음)
mVibrator.cancel();   // 진동 중지*/
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

        playMusic();    // 음악 및 진동 시작

        return START_STICKY;

    }



    @Override

    public void onDestroy() {

        super.onDestroy();

       stopMusic();   // 음악 및 진동 중지

        // Power off
       WakeLockUtil.releaseCpuWakeLock();

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private void playMusic() {

        stopMusic();  // 플레이 할 때 가장 먼저 음악 중지 실행

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);  // 기본 벨소리(알람)의 URI

        mMediaPlayer = new MediaPlayer();  // 1. MediaPlayer 객체 생성



        try {

            mMediaPlayer.setDataSource(this, alert);  // 2. 데이터 소스 설정 (인터넷에 있는 음악 파일도 가능함)

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
    /*
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {

            case REQUEST_CODE_RINGTONE :

                if(resultCode == RESULT_OK ) {

                    // 선택한 Ringtone(벨소리)를 받아온다.

                    Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    // 벨소리 이름 얻는 방법

                    String ringToneName = RingtoneManager.getRingtone(this, uri).getTitle(this);

                }

        }

    }*/

}
