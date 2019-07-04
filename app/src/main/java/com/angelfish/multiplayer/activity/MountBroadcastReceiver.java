package com.angelfish.multiplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.angelfish.multiplayer.services.CopyLocalFilesService;


public class MountBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "MultiPlayer";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){
            Log.e(TAG, "Intent.ACTION_MEDIA_MOUNTED");
            Intent startServiceIntent = new Intent();  // 要启动的Activity
            startServiceIntent = new Intent(context, CopyLocalFilesService.class);
            context.startService(startServiceIntent);

        }
    }






}
