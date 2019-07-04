package com.angelfish.multiplayer.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.angelfish.multiplayer.services.CopyLocalFilesService;
import com.angelfish.multiplayer.util.AddressUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class BootBroadcastReceiver extends BroadcastReceiver {

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
        }
    }






}
