package com.example.lg.deepdreamer;

import android.os.Handler;

/**
 * Created by lg on 2018-08-13.
 */

//자이로,녹음 서비스 스레드
public class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;

    public ServiceThread(Handler handler){
        this.handler = handler;
    }

    public void stopForever(){
        synchronized (this){
            this.isRun = false;
        }
    }
    public void run(){
        while (isRun){
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(10000);
            }catch (Exception e){}
        }
    }
}
