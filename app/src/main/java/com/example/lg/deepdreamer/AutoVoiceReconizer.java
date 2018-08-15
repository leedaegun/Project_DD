package com.example.lg.deepdreamer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;


/**
 * Created by lg on 2018-08-03.
 */

//일정 소음이상 자동 측정 녹음
public class AutoVoiceReconizer {
    public static final int VOICE_READY = 1;
    public static final int VOICE_RECONIZING = 2;
    public static final int VOICE_RECONIZED = 3;
    public static final int VOICE_RECORDING_FINSHED = 4;
    public static final int VOICE_PLAYING = 5;
    public static final int FILE_PATH = 6;

    RecordAudio recordTask;
    PlayAudio playTask;
    final int CUSTOM_FREQ_SOAP = 2;;

    File recordingFile;

    boolean isRecording = false;
    boolean isPlaying = false;

    int frequency = 11025;
    int outfrequency = frequency*CUSTOM_FREQ_SOAP;
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferReadResult;

    private Handler handler;
    //private Calendar mCalendar;
    //파일 저장 인덱스 -> 저장날짜
    //private  int tmp_year,tmp_month,tmp_date,tmp_hour,tmp_mintue;


    LinkedList<short[]> recData = new LinkedList<short[]>();

    int level; // 볼륨레벨
    private int startingIndex = -1; // 녹음 시작 인덱스
    private int endIndex = -1;
    private int cnt = 0;// 카운터

    private boolean voiceReconize = false;

    public AutoVoiceReconizer( Handler handler ){
        /*
        mCalendar = Calendar.getInstance();

        //파일 구분을 위한 파일이 저장된 날짜
        tmp_year=mCalendar.get(Calendar.YEAR);
        tmp_month=mCalendar.get(Calendar.MONTH)+1;
        tmp_date=mCalendar.get(Calendar.DATE);

        tmp_hour = mCalendar.get(Calendar.HOUR);
        tmp_mintue = mCalendar.get(Calendar.MINUTE);
*/
        this.handler = handler;
        File path = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/DeepDreamer/");
        path.mkdirs();
        try {
            recordingFile = File.createTempFile("recording", ".mp4", path);//""+tmp_year + "/" + tmp_month+ "/" + tmp_date + "/"+tmp_hour+":" + tmp_mintue+

        } catch (IOException e) {
            throw new RuntimeException("Couldn't create file on SD card", e);
        }
    }

    public void startLevelCheck(){
        voiceReconize = false;
        cnt = 0;
        startingIndex = -1;
        endIndex = -1;
        recData.clear();
        recordTask = new RecordAudio();
        recordTask.execute();
        isRecording = true;

    }

    public void stopLevelCheck(){
        short[] buffer = null;

        isRecording = false;

        try {
            DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(
                            recordingFile)));

            Log.i("test", "startingIndex = " + startingIndex + " endIndex = " + endIndex );
            for( int i = startingIndex ; i < endIndex ; i++ ){
                buffer = recData.get( i );
                for( int j = 0 ; j < bufferReadResult ; j++ ){
                    dos.writeShort( buffer[ j ] );
                }
            }

            dos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg1 = handler.obtainMessage(FILE_PATH,String.valueOf(recordingFile));
        handler.sendMessage( msg1 );



    }

    public void playVoice(String filename){

        //선택한 파일 재생
        Message msg = handler.obtainMessage( VOICE_PLAYING );
        handler.sendMessage( msg );
        Log.i("playVoice : 클릭한 파일 이름",filename);
        File inputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepDreamer/" +filename);
        Log.i("playVoice : 인풋값 파일 경로",Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeepDreamer/" +filename);
        playTask = new PlayAudio();
        playTask.execute(inputFile);
    }

    private class PlayAudio extends AsyncTask<File, Integer, Void> {
        @Override
        protected Void doInBackground(File... params) {
            isPlaying = true;
            Log.i("함수안 파일 경로",  params[0].toString());

            int bufferSize = AudioTrack.getMinBufferSize(outfrequency,//1.5뺌
                    channelConfiguration, audioEncoding);
            short[] audiodata = new short[bufferSize / 4];

			/*
			int bufferSize = AudioTrack.getMinBufferSize((int)(outfrequency),
					channelConfiguration, audioEncoding);
			short[] audiodata = new short[bufferSize / 4];
			*/

            try {
                DataInputStream dis = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(
                                params[0])));//recordingFile -> 경로 입력값 변경

                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,  outfrequency ,//1.5곱한거 뺌
                        channelConfiguration, audioEncoding, bufferSize,
                        AudioTrack.MODE_STREAM);
                /* 대체 함수
                AudioTrack audioTrack = new AudioTrack(new AudioAttributes
                        .Builder().setUsage()
                        .setContentType()
                        .build(),
                        audioEncoding, bufferSize,
                        AudioTrack.MODE_STREAM);
                ///////////////////// 약간 목소리가 변형되어 나옴.. * 1.5 를 빼면 원본 목소리가 나옴 /////
				/*
				AudioTrack audioTrack = new AudioTrack(
						AudioManager.STREAM_MUSIC, (int) (outfrequency * 1.5),
						channelConfiguration, audioEncoding, bufferSize,
						AudioTrack.MODE_STREAM);
						*/

                audioTrack.play();

                while (isPlaying && dis.available() > 0) {
                    int i = 0;
                    while (dis.available() > 0 && i < audiodata.length) {
                        audiodata[i] = dis.readShort();
                        i++;
                    }
                    audioTrack.write(audiodata, 0, audiodata.length);
                }

                dis.close();

            } catch (Throwable t) {
                Log.e("AudioTrack", "Playback Failed");
            }

            //Message msg = handler.obtainMessage( VOICE_READY );
            //handler.sendMessage( msg );

            return null;
        }
    }

    private class RecordAudio extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            Message msg = null;
            try {

                msg = handler.obtainMessage( VOICE_RECONIZING );
                handler.sendMessage( msg );

                DataOutputStream dos = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(
                                recordingFile)));
                int bufferSize = AudioRecord.getMinBufferSize(outfrequency,
                        channelConfiguration, audioEncoding);
                AudioRecord audioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC, outfrequency,
                        channelConfiguration, audioEncoding, bufferSize);
                short[] buffer = null;
                audioRecord.startRecording();
                int total = 0;
                buffer = new short[bufferSize];
                while (isRecording) {
                    buffer = new short[bufferSize];
                    bufferReadResult = audioRecord.read(buffer, 0,
                            bufferSize);
                    total = 0;
                    for (int i = 0; i < bufferReadResult; i++) {
                        total += Math.abs(buffer[i]);
                    }
                    recData.add( buffer );
                    level = (int) ( total / bufferReadResult );

                    // level 은 볼륨..
                    // level 값이 2000이 넘은 경우 목소리를 체크를 시작 ->1000으로 변경 08.05
                    // 2000이 넘는 상태에서 cnt 를 증가시켜 10회 이상 지속되면 목소리가 나는 것으로 간주함
                    // voiceReconize 가 활성화 되면 시작 포인트
                    if( voiceReconize == false ){
                        if( level > 2000 ){
                            if( cnt == 0 )
                                startingIndex = recData.size();
                            cnt++;
                        }

                        if( cnt > 10 ){
                            cnt = 0;
                            voiceReconize = true;
                            // level 값이 처음으로 1000 값을 넘은시점으로부터 15 만큼 이전부터 플레이 시점 설정
                            // 시작하는 목소리가 끊겨 들리지 않게 하기 위하여 -15
                            startingIndex -= 15;
                            if( startingIndex < 0 )
                                startingIndex = 0;

                            msg = handler.obtainMessage( VOICE_RECONIZED );
                            handler.sendMessage( msg );
                        }
                    }

                    if( voiceReconize == true ){
                        // 목소리가 끝나고 500이하로 떨어진 상태가 40이상 지속된 경우
                        // 더이상 말하지 않는것으로 간주.. 레벨 체킹 끝냄
                        if( level < 500 ){
                            cnt++;
                        }
                        // 도중에 다시 소리가 커지는 경우 잠시 쉬었다가 계속 말하는 경우이므로 cnt 값은 0
                        if( level > 2000 ){//1000으로 변경 08.05
                            cnt = 0;
                        }
                        // endIndex 를 저장하고 레벨체킹을 끝냄
                        if( cnt > 40 ){
                            endIndex = recData.size();
                            isRecording = false;

                            msg = handler.obtainMessage( VOICE_RECORDING_FINSHED );
                            handler.sendMessage( msg );
                        }
                    }
                }
                audioRecord.stop();
                dos.close();
            } catch (Exception e) {
                Log.e("AudioRecord", "Recording Failed");
                Log.e("AudioRecord", e.toString() );
            }

            return null;
        }

        protected void onPostExecute(Void result) {
        }
    }
}
