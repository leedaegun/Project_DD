package com.example.lg.deepdreamer.util;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by lg on 2018-08-17.
 */

public class WakeLockUtil {
    // 디바이스 깨우기 위한 클래스
    private  static PowerManager.WakeLock mCpuWakeLock;
    private  static  final String TAG = "DeepDreamerAlarm";

    public static void acquireCpuWakeLock(Context context){

        if(mCpuWakeLock!=null)return;
        if(!isScreenOn(context)){
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mCpuWakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP|
                PowerManager.ON_AFTER_RELEASE,TAG);//화면 켜짐
        mCpuWakeLock.acquire();}
    }
    public static void releaseCpuWakeLock(){
        if(mCpuWakeLock!=null){
            mCpuWakeLock.release();
            mCpuWakeLock=null;
        }
    }
    public static boolean isScreenOn(Context context) {
        return ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).isInteractive();
    }
}
