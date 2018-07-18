package com.example.lg.deepdreamer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class AlarmService extends Service {
    public AlarmService() {
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
}
