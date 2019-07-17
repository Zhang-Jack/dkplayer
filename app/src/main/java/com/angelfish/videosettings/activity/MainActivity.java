package com.angelfish.videosettings.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;

import android.location.Address;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.angelfish.videosettings.R;




import com.google.android.exoplayer2.offline.Downloader;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import android.provider.Settings.Secure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MultiPlayer";

    private Context mContext;
    private boolean mIsWritingPermitted = false;
    private boolean mIsReadingPermitted = false;
    private boolean mIsReadPhoneStatePermitted = false;
    private TelephonyManager mTelephonyManager;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        isWriteStoragePermissionGranted();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_multi_player);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.hide();
        }
        checkPermissionsAndStartIntent();
    }

    public  boolean isReadStoragePermissionGranted() {
        boolean isPermitted = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                mIsReadingPermitted = true;
                isPermitted = true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                isPermitted =false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            mIsReadingPermitted = true;
            isPermitted = true;
        }
        if(mIsReadingPermitted == true){
            isReadPhoneStateGranted();

        }
        return isPermitted;
    }

    public  boolean isWriteStoragePermissionGranted() {
        boolean isPermitted = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                mIsWritingPermitted = true;
                isPermitted = true;

            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                isPermitted = false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            mIsWritingPermitted = true;
            isPermitted =true;
        }
        if(mIsWritingPermitted == true){
            isReadStoragePermissionGranted();
        }
        return isPermitted;
    }

    private boolean isReadPhoneStateGranted(){
        boolean isPermitted = false;
        mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
//                mESN_Number = mTelephonyManager.getDeviceId();
                mIsReadPhoneStatePermitted = true;
                isPermitted = true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 4);
                isPermitted = false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
//            mESN_Number = mTelephonyManager.getDeviceId();
            mIsReadPhoneStatePermitted = true;
            isPermitted  =true;
        }

//        Log.i(TAG, "ESN = "+mESN_Number);

        return isPermitted;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults == null || grantResults.length == 0){
            Log.e(TAG, "grantResults = null return!");
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    mIsWritingPermitted =true;
                    isReadStoragePermissionGranted();

                }else{
                    finish();
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    mIsReadingPermitted = true;
                    isReadPhoneStateGranted();
//                    checkPermissionsAndStartIntent();
                }else{
                    finish();
                }
                break;

            case 4:
                Log.d(TAG, "Read Phone State");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                                == PackageManager.PERMISSION_GRANTED) {
//                            mESN_Number = mTelephonyManager.getDeviceId();
                            mIsReadPhoneStatePermitted = true;
                            checkPermissionsAndStartIntent();
                        }
                    }
                }else{
                    finish();
                }
                break;
        }
    }

    public void checkPermissionsAndStartIntent(){
        if(mIsReadPhoneStatePermitted && mIsReadingPermitted && mIsWritingPermitted){
            Intent startPlayer = new Intent();
            startPlayer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startPlayer.setClass(mContext, SettingsActivity.class);


            mContext.startActivity(startPlayer);
        }
    }

}
