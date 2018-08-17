package com.example.lg.deepdreamer;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

/**
 * Created by lg on 2018-08-17.
 */

public class WakeLockUtil {

    private  static PowerManager.WakeLock mCpuWakeLock;
    private  static  final String TAG = "DeepDreamerAlarm";
    static void acquireCpuWakeLock(Context context){
        ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                /*This constant was deprecated in API level 26. Use FLAG_SHOW_WHEN_LOCKED or
        KeyguardManager.requestDismissKeyguard(Activity, KeyguardManager.KeyguardDismissCallback) instead.
            Since keyguard was dismissed all the time as long as an activity with this flag on its window was focused, k
        eyguard couldn't guard against unintentional touches on the screen, which isn't desired.
        */
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,TAG);
        mCpuWakeLock.acquire();
    }
    static void releaseCpuWakeLock(){
        if(mCpuWakeLock!=null){
            mCpuWakeLock.release();
            mCpuWakeLock=null;
        }
    }
}
