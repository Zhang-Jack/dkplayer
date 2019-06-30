package com.angelfish.multiplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;




public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "MultiPlayer";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e(TAG, "onReceive android.intent.action.BOOT_COMPLETED");
            Intent startIntent = new Intent();  // 要启动的Activity
            //1.如果自启动APP，参数为需要自动启动的应用包名
//            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            //下面这句话必须加上才能开机自动运行app的界面
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.setClass(context, MainActivity.class);
            //2.如果自启动Activity
            context.startActivity(startIntent);
            //3.如果自启动服务
//            context.startService(startIntent);
        }else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){
            Log.e(TAG, "Intent.ACTION_MEDIA_MOUNTED");
        }
    }
}
