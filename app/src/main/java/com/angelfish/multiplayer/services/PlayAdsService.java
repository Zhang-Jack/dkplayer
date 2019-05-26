package com.angelfish.multiplayer.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import com.angelfish.multiplayer.activity.MainActivity;

import java.util.Date;

public class PlayAdsService extends Service {
    private Handler mHandler01 = new Handler();
    private Handler mHandler02 = new Handler();

    /* 上一次User有动作的Time Stamp */
    private Date lastUpdateTime;
    /* 计算User有几秒没有动作的 */
    private long timePeriod;

    /* 静止超过N秒将自动进入屏保 */
    private float mHoldStillTime = 5;
    /*标识当前是否进入了屏保*/
    private boolean isRunScreenSaver;

    /*时间间隔*/
    private long intervalScreenSaver = 1000;
    private long intervalKeypadeSaver = 1000;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("LongRunningService", "executed at " + new Date().
                        toString());
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 50 * 1000; // 这是50sec的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }



}
