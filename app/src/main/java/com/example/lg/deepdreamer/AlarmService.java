package com.example.lg.deepdreamer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AlarmService extends Service {
    private static final String TAG = AlarmService.class.getSimpleName();

    private final IBinder mBinder = new Binder();


    public AlarmService() {
    }
    @Override
    public void onCreate() {
        Log.e("LOG", "onCreate()");
        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        Toast.makeText(this,"알람이 울립니다.",Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        Log.e("LOG", "onDestroy()");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("LOG", "onUnbind()");
        return super.onUnbind(intent);
    }




}
