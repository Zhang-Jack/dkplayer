package com.angelfish.multiplayer.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.telecom.Call;
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

import com.angelfish.multiplayer.R;
import com.angelfish.multiplayer.bean.VideoBean;
import com.angelfish.multiplayer.util.MySqliteHelper;
import com.angelfish.videocontroller.StandardVideoController;
import com.angelfish.videoplayer.listener.OnVideoViewStateChangeListener;
import com.angelfish.videoplayer.player.IjkVideoView;
import com.angelfish.multiplayer.BuildConfig;
import com.angelfish.multiplayer.util.AddressUtils;
import com.google.android.exoplayer2.offline.Downloader;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import android.provider.Settings.Secure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class PlayerActivity extends AppCompatActivity{

    private static final String TAG = "MultiPlayer";
    private IjkVideoView mPlayer1;

    static private int backpressed = 0;
    private Context mContext;
    private boolean mIsPaused = false;
    private List<String> mFileList_1 = new ArrayList<>();
    private List<String> mParserFromJson = new ArrayList<>();
    protected PowerManager.WakeLock mWakeLock;

    private int mPlayer_index1 = 0;

    private boolean mTheFirstTimeRunning = true;
    private boolean mTheFirstShowToast = true;
    private int mDownloadFilesCount = 0;


    private String VOD_URL_1 = "";


    private static final int UPDATE_INTERVAL = 60;
    private String BASE_URL = "http://projector.auong.com/";
    private static final String SettingsPref = "settings_pref";
    private static final String AddressKey = "AddressKey";
    private static final String ModeKey = "ModeKey";
    private static final String Update_Interval_Key = "Update_Interval_Key";
    private static final String DownloadFolder = "/MultiPlayer/";
    private static final String LocalFolder = "/LocalVideos/";
    private static final String ServerFile = "/server.cfg";
    private SharedPreferences mSettingsSP;
    private TelephonyManager mTelephonyManager ;
    //    private String mESN_Number = "";
    private String mPlayMode = "All";
    //    private boolean mLastCheckResult = false;
    private MySqliteHelper mHelper = null;
    private boolean mNeedUpdateIndex = false;
    private int mFilesInWebFolder = 0;
    private int mFilesInLocalFolder = 0;
    private int mUpdateInterval = UPDATE_INTERVAL;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        isWriteStoragePermissionGranted();
        isReadStoragePermissionGranted();
        isReadPhoneStateGranted();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_singleplayer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_multi_player);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.hide();
        }
        mSettingsSP = getSharedPreferences(SettingsPref, MODE_PRIVATE);
//        String addrPref = mSettingsSP.getString(AddressKey, "");
        String addrFromFile = readStringFromFile();
        if(!addrFromFile.equals("")){
            BASE_URL = addrFromFile;
        }

        String modePref = mSettingsSP.getString(ModeKey, "");
        if(!modePref.equals("")){
            mPlayMode = modePref;
        }else{
            mSettingsSP.edit().putString(ModeKey, mPlayMode).apply();
        }

        VOD_URL_1 = "android.resource://" + getPackageName() + "/" + R.raw.movie;
        mPlayer1 = findViewById(R.id.player_1);
        mPlayer1.setUrl(VOD_URL_1);

        mFileList_1.add(VOD_URL_1);
        mUpdateInterval = mSettingsSP.getInt(Update_Interval_Key, UPDATE_INTERVAL);


        checkForUpdateResources();
        checkForUpdateAds();

        startPlayingVideo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(;;) {

                        Thread.sleep(mUpdateInterval*1000); // 休眠60秒
                        Log.e(TAG, "routine check mDownloadFilesCount = "+mDownloadFilesCount);
                        if (mDownloadFilesCount == 0) {
                            Log.e(TAG, "files number to display = "+mFileList_1.size());
                            checkForLocalUpdate();
                            checkForUpdateAds();
                            checkForPlayModes();
                        }
                    }
                }catch(InterruptedException ex){
                    ex.printStackTrace();

                }
            }
        }).start();


    }
    public void checkForPlayModes(){
        String playMode = mSettingsSP.getString(ModeKey,"");
        if(!playMode.equals(mPlayMode)){
            Log.i(TAG, "play mode has been changed: "+playMode);
            if(!isServiceRunning("com.angelfish.multiplayer.services.CopyLocalFilesService")){
                checkForUpdateResources();

            }
        }
    }

    public String readStringFromFile(){
        String webSite = "";
        File serverConfig = new File(Environment.getExternalStorageDirectory() + ServerFile);
        if (serverConfig.exists() ){
            try {
                FileInputStream fis = new FileInputStream(serverConfig);
                StringBuilder stringBuilder = new StringBuilder();
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                while ( (webSite = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(webSite);
                }
                fis.close();
                webSite = stringBuilder.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
        return webSite;
    }

    public void saveNewWebsiteToFile(String website){
        FileOutputStream FoutS = null;
        OutputStreamWriter outSW = null;

        try {
            FoutS = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+ServerFile));
            outSW = new OutputStreamWriter(FoutS);

            outSW.write(website);

            outSW.flush();
            // Rest of try block here
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {

            try {

                outSW.close();

                FoutS.close();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }


    }

    public void checkForLocalUpdate(){
        boolean isServiceRunning = isServiceRunning("com.angelfish.multiplayer.services.CopyLocalFilesService");
        Log.i(TAG, "isServiceRunning = " +isServiceRunning);
        File localpath = new File(Environment.getExternalStorageDirectory()+LocalFolder);
        if(!localpath.exists()){
            return;
        }
        if(isServiceRunning == true){
            return;
        }
        File[] localFiles = localpath.listFiles();
        if(localFiles.length == 0){
            return;
        }
        if (isServiceRunning == false){
            if (mPlayMode.equals("All")) {
                updatePlayListwithLocalFiles();
            }else if(mPlayMode.equals("Local")){
                mFileList_1.clear();
                updatePlayListwithLocalFiles();
            }
        }
//        mLastCheckResult = isServiceRunning;
    }

    public void updatePlayListwithLocalFiles(){
        File f = new File(Environment.getExternalStorageDirectory() + LocalFolder);
        AddressUtils.checkFilePath(f);
//        File dir1 = new File(f.getPath()+"/Player1/");
//        AddressUtils.checkFilePath(dir1);
        File[] files = f.listFiles();
        int fileAdded = 0;

        for (int i = 0; i < files.length; i++){
            if(!mFileList_1.contains(f.getAbsolutePath()+"/"+files[i].getName())) {
                mFileList_1.add(f.getAbsolutePath()+"/"+ files[i].getName());
                fileAdded ++;
            }

        }
        if(fileAdded>0) {
            mNeedUpdateIndex = true;
        }
    }


    public void checkForUpdateResources(){
//        File assets = new File("android.resource://" + getPackageName() + "/");
//        File[] default_movies = assets.listFiles();
//        for (int i = 0; i < default_movies.size(); i++){
//            mFileList_1.add(assets.getAbsolutePath()+"/"+default_movies[i].getName());
//
//        }
        if(mTheFirstTimeRunning) {
            Toast.makeText(mContext, R.string.str_start_checking, Toast.LENGTH_SHORT).show();
            mTheFirstTimeRunning = false;
        }
        if(mPlayMode.equals("All")|| mPlayMode.equals("Web")) {
            File f = new File(Environment.getExternalStorageDirectory() +DownloadFolder);
            AddressUtils.checkFilePath(f);
//        File dir1 = new File(f.getPath()+"/Player1/");
//        AddressUtils.checkFilePath(dir1);
            File[] files = f.listFiles();
            mFilesInWebFolder = files.length;
            if (files.length > 0) {
                if (mFileList_1.contains(VOD_URL_1)) {
                    mFileList_1.remove(VOD_URL_1);
                } else if (mDownloadFilesCount == 0) {
                    Log.i(TAG, "Update Play list when no downloading");
                    mFileList_1.clear();
                }
            }


            for (int i = 0; i < files.length; i++) {
                if(!mFileList_1.contains(f.getAbsolutePath()+"/"+files[i].getName())) {
                    mFileList_1.add(f.getAbsolutePath() + "/" + files[i].getName());
                }
            }

        }

        if(mPlayMode.equals("All")|| mPlayMode.equals("Local")) {
            if (mFileList_1.contains(VOD_URL_1)){
                mFileList_1.remove(VOD_URL_1);
            }
            updatePlayListwithLocalFiles();
        }


        if (mFileList_1.size() == 0)
        {
            Log.i(TAG, "No file found in the dir!");
            mFileList_1.add(VOD_URL_1);
//                Toast.makeText(mContext, "No file found in the dir!!", Toast.LENGTH_LONG).show();
        }
        mPlayer1.setUrl(mFileList_1.get(0));

    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }

    private boolean isReadPhoneStateGranted(){
        mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
//                mESN_Number = mTelephonyManager.getDeviceId();
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 4);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
//            mESN_Number = mTelephonyManager.getDeviceId();

        }

//        Log.i(TAG, "ESN = "+mESN_Number);
        return true;
    }

    /**
     * 判断服务是否运行
     */
    private boolean isServiceRunning(final String className) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if (className.equals(aInfo.service.getClassName())) return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission

                }else{
                    finish();
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission

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
                        }
                    }
                }else{
                    finish();
                }
                break;
        }
    }

    public void startPlayingVideo(){
        mPlayer1.setEnableAudioFocus(false);
        mPlayer1.setUsingSurfaceView(false);
//        StandardVideoController controller1 = new StandardVideoController(this);
//        mPlayer1.setVideoController(controller1);
        //高级设置（可选，须在start()之前调用方可生效）
//        mPlayer1.setLooping(true);


        mPlayer1.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {
                switch (playerState) {
                    case IjkVideoView.PLAYER_NORMAL://小屏
                        break;
                    case IjkVideoView.PLAYER_FULL_SCREEN://全屏
                        break;
                }
            }
            @Override
            public void onPlayStateChanged(int playState) {
                switch (playState) {
                    case IjkVideoView.STATE_IDLE:
                        break;
                    case IjkVideoView.STATE_PREPARING:
                        break;
                    case IjkVideoView.STATE_PREPARED:
                        break;
                    case IjkVideoView.STATE_PLAYING:

                        mPlayer1.setVisibility(View.VISIBLE);

                        break;
                    case IjkVideoView.STATE_PAUSED:
                        break;
                    case IjkVideoView.STATE_BUFFERING:
                        break;
                    case IjkVideoView.STATE_BUFFERED:
                        break;
                    case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                        mPlayer_index1 ++;
                        if(mPlayer_index1 >=mFileList_1.size())
                            mPlayer_index1 = 0;
                        mPlayer1.release();

                        if(mNeedUpdateIndex){
                            mPlayer_index1 = mFilesInWebFolder;
                            mNeedUpdateIndex = false;
                        }
                        mPlayer1.setUrl(mFileList_1.get(mPlayer_index1));
                        mPlayer1.start();

                        break;
                    case IjkVideoView.STATE_ERROR:
                        File fileCausedErr = new File(mFileList_1.get(mPlayer_index1));
                        fileCausedErr.delete();
                        mFileList_1.remove(mPlayer_index1);
                        if(mFileList_1.size() == 0){
                            mFileList_1.add(VOD_URL_1);
                        }
                        mPlayer_index1 ++;
                        if(mPlayer_index1 >=mFileList_1.size())
                            mPlayer_index1 = 0;
                        mPlayer1.release();

                        mPlayer1.setUrl(mFileList_1.get(mPlayer_index1));
                        mPlayer1.start();
                        break;
                }
            }
        });

        mPlayer1.start();
        mPlayer1.setVisibility(View.GONE);
    }

    public String getDeviceManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        return capitalize(manufacturer);

    }

    public String getDeviceName() {
        String name = Build.MODEL;
        return capitalize(name);

    }

    public String getDeviceSerial() {
        String device_sn = Build.SERIAL;
        if (Build.VERSION.SDK_INT >= 26) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    Class<?> c = Class.forName("android.os.SystemProperties");
                    Method get = c.getMethod("get", String.class);
                    Log.e(TAG, "before device_sn = "+device_sn);

                    device_sn = (String) get.invoke(c, "gsm.sn1");
                    if (device_sn.equals(""))
                        device_sn = (String) get.invoke(c, "ril.serialnumber");
                    if (device_sn.equals(""))
                        device_sn = (String) get.invoke(c, "ro.serialno");
                    if (device_sn.equals(""))
                        device_sn = (String) get.invoke(c, "sys.serialnumber");
                    if (device_sn.equals(""))
                        device_sn = Build.getSerial();
                    Log.e(TAG, "after device_sn = "+device_sn);
                } catch (Exception e) {
                    e.printStackTrace();
                    device_sn = "";
                }
            }
        }
        return capitalize(device_sn);

    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        mPlayer1.pause();
        /*mPlayer2.pause();
        mPlayer3.pause();
        mPlayer4.pause();
        mPlayer5.pause();*/
        mIsPaused = true;
    }

    @Override
    protected void onResume() {
        if(mIsPaused){
            mPlayer1.release();
            /*mPlayer2.release();
            mPlayer3.release();
            mPlayer4.release();
            mPlayer5.release();*/
            startPlayingVideo();
            mIsPaused = false;
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer1.release();
        /*mPlayer2.release();
        mPlayer3.release();
        mPlayer4.release();
        mPlayer5.release();*/
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        String keycode_hint = "keyCode = "+keyCode;
        Log.i(TAG, keycode_hint);
//        Toast.makeText(mContext, keycode_hint,Toast.LENGTH_SHORT).show();
        if(keyCode == KeyEvent.KEYCODE_BACK){
            backpressed ++;
            if(backpressed < 2) {
                Toast.makeText(mContext, R.string.str_press_again_hint, Toast.LENGTH_SHORT).show();
                return true;
            }
            backpressed = 0;
            super.onKeyDown(keyCode, event);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_MENU){
//            Toast.makeText(mContext, "POPUP MENU", Toast.LENGTH_SHORT).show();
//            PopupMenu popup = new PopupMenu(MainActivity.this, mPlayer2);
            PopupMenu popup = new PopupMenu(PlayerActivity.this, mPlayer1);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId()==R.id.update_ads){
                        checkForUpdateAds();
                    }else if(item.getItemId()==R.id.change_layout){

                    }else if(item.getItemId() == R.id.version_info){
                        int versionCode = BuildConfig.VERSION_CODE;
                        String versionName = BuildConfig.VERSION_NAME;
                        AlertDialog alertDialog = new AlertDialog.Builder(PlayerActivity.this).create();
                        alertDialog.setTitle(R.string.str_version_info);
                        alertDialog.setMessage(versionName);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
//                        builder = new AlertDialog.Builder(mContext);
//                        builder.setTitle(R.string.str_version_info);
//                        int versionCode = BuildConfig.VERSION_CODE;
//                        String versionName = BuildConfig.VERSION_NAME;
//                        builder.setMessage("versionCode = "+versionCode+"\n versionName ="+versionName);
//                        AlertDialog alert = builder.create();
//                        alert.setTitle(R.string.str_version_info);
//                        alert.show();
                    }
//                    Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            popup.show();//showing popup menu
            super.onKeyDown(keyCode, event);
            return true;
        }
        return false;
    }

    public void checkForUpdateAds(){
        String android_id = Secure.getString(mContext.getContentResolver(),
                Secure.ANDROID_ID);
        Log.i(TAG, "length of device id ="+android_id);
        if(android_id.length()==15){
            android_id = android_id+"0";
        }

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        int rssi = 0;
        String IPAddress = AddressUtils.getIPAddress(true);
        String macAddress = AddressUtils.getMACAddress("wlan0");
        if(macAddress.equals("")){
            macAddress = AddressUtils.getMACAddress("eth0");

        }else {
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            rssi = wifiInfo.getRssi();
        }
        long timestamp = Calendar.getInstance().getTimeInMillis();
        String manufacturer = getDeviceManufacturer();
        String device_name = getDeviceName();
        String device_sn = getDeviceSerial();
        String ativate_string = BASE_URL+"/?act=api/device!activate&mac_addr="+macAddress+"&device_id="+android_id+"&version_code="+versionCode+"&version_name="+versionName+"&address="+IPAddress+"&timestamp="+timestamp+"&manufacturer="+manufacturer+"&device_name="+device_name+"&device_sn="+device_sn+"&rssi="+rssi;
        Log.e(TAG, ativate_string);
        try{
            URL ativate_link = new URL(ativate_string);
            new GetTokenTask().execute(ativate_link);


        }catch(MalformedURLException ex){
            ex.printStackTrace();
        }
        return;
//        try{
//        URL test_link = new URL("http://projector.auong.com/i/r/201906011836258919.mp4");
//        new DownloadFilesTask().execute(test_link);
//        }catch(MalformedURLException ex){
//            ex.printStackTrace();
//        }
    }

    public String getTimestamp() {
        String timestamp = DateFormat.getDateTimeInstance().format(new Date());
        return timestamp;
    }

    private class DownloadFilesTask extends AsyncTask<URL, Integer, String> {
        protected String doInBackground(URL... urls) {
            int count = urls.length;
            long totalSize = 0;
            String fileName = "";
            try{
                for (int i = 0; i < count; i++) {
//                    URLConnection conexion = urls[i].openConnection();
//                    conexion.connect();
                    OkHttpClient client = new OkHttpClient();

                    try{
                        Response response = client.newCall(new Request.Builder().url(urls[i]).get().build()).execute();
                        long lenghtOfFile = response.body().contentLength();
                        Log.d(TAG, "Lenght of file: " + lenghtOfFile);
                        totalSize += lenghtOfFile;
                        fileName = urls[i].toString().substring(urls[i].toString().lastIndexOf("/") + 1);

//                        InputStream input = new BufferedInputStream(conexion.getInputStream());
                        InputStream input = new BufferedInputStream(response.body().byteStream());
                        OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + DownloadFolder+fileName);
                        Log.d(TAG, "save to temp ");
                        byte data[] = new byte[1024*5];

                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;
    //                        Log.d(TAG, "downlaod bytes: " + total);
                            publishProgress((int)((total*100)/lenghtOfFile));
                            output.write(data, 0, count);
                        }

                        output.flush();
                        output.close();
                        input.close();
                    }catch (IOException ignore) {
                        return null;
                    }
                }
            }catch(Exception e){
                if(!fileName.equals("")){
                    File f = new File(Environment.getExternalStorageDirectory() +DownloadFolder+fileName);
                    f.delete();
                }
                mDownloadFilesCount--;
                return null;
            }
            return fileName;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String fileName) {
//            showDialog("Downloaded " + result + " bytes");
            if(fileName == null){
                Toast.makeText(mContext, "Download error", Toast.LENGTH_LONG).show();
            }
//            Toast.makeText(mContext,"Download finished!", Toast.LENGTH_LONG).show();
            if(mDownloadFilesCount >0){
                mDownloadFilesCount--;
            }
            Toast.makeText(mContext, fileName+getString(R.string.str_downloaded), Toast.LENGTH_LONG).show();
            mFileList_1.add(Environment.getExternalStorageDirectory() + DownloadFolder+fileName);
            //重新设置数据
            if(mFileList_1.contains(VOD_URL_1)){
                mFileList_1.remove(VOD_URL_1);
            }
            mPlayer1.release();

//            mPlayer1.setVideoController(mStandardVideoController);
            //开始播放
            mPlayer1.setUrl(mFileList_1.get(mFileList_1.size()-1));
            mPlayer1.start();
        }
    }

    private class GetTokenTask extends AsyncTask<URL, Integer, JSONObject> {
        protected JSONObject doInBackground(URL... urls) {
            int count = urls.length;

            try{
                for (int i = 0; i < count; i++) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(false)
                            .build();
                    Response response = client.newCall(new Request.Builder()
                            .url(urls[i])
                            .build()).execute();
                    try{
                        String result = response.body().string();
                        Log.e(TAG, result);
                        return new JSONObject(result);
                    }finally {
                        response.close();

                    }

//                    URLConnection conexion = urls[i].openConnection();
//                    conexion.setRequestProperty("Connection", "close");
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
//
//                    StringBuffer stringBuffer = new StringBuffer();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null)
//                    {
//                        stringBuffer.append(line);
//                    }

//                    return new JSONObject(stringBuffer.toString());
                }
            }catch(Exception ex){
//                Toast.makeText(mContext, R.string.str_connection_error, Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                return null;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(JSONObject response) {
            int type = 0;
//            showDialog("Downloaded " + result + " bytes");
            if(response != null)
            {
                try {
                    String token = response.getString("token");
                    String resource_string = BASE_URL+"/?act=api/resource&type="+type+"&token="+token;
                    String remote_url = response.getString("url")+"/";
//                    mSettingsSP.edit().putString(AddressKey, remote_url).apply();
                    saveNewWebsiteToFile(remote_url);
                    String play_mode = response.getString("play_order");
                    setPlayMode(play_mode);
                    mUpdateInterval = response.getInt("heartbeat");
                    mSettingsSP.edit().putInt(Update_Interval_Key, mUpdateInterval).apply();
                    Log.i(TAG, "remote url ="+remote_url);
                    Log.i(TAG, "interval ="+mUpdateInterval);
                    Log.i(TAG, "play_mode = "+play_mode);

                    try {
                        URL resource_link = new URL(resource_string);
                        new GetVideoJsonTask().execute(resource_link);
                        Log.e(TAG, "Success: " + token);
                    }catch(MalformedURLException ex){
                        ex.printStackTrace();
                    }

                } catch (JSONException ex) {
                    Log.e(TAG, "Failure", ex);
                }
            }
        }

    }

    private class GetVideoJsonTask extends AsyncTask<URL, Integer, JSONObject> {
        protected JSONObject doInBackground(URL... urls) {
            int count = urls.length;

            try{
                for (int i = 0; i < count; i++) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(false)
                            .build();
                    Response response = client.newCall(new Request.Builder()
                            .url(urls[i])
                            .build()).execute();
                    try{
                        String result = response.body().string();
                        Log.e(TAG, result);
                        return new JSONObject(result);
                    }finally {
                        response.close();

                    }

//                    URLConnection conexion = urls[i].openConnection();
//                    conexion.setRequestProperty("Connection", "close");
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
//
//                    StringBuffer stringBuffer = new StringBuffer();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null)
//                    {
//                        stringBuffer.append(line);
//                    }
//
//                    return new JSONObject(stringBuffer.toString());
                }
            }catch(Exception ex){
                ex.printStackTrace();
                return null;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(JSONObject response) {
//            showDialog("Downloaded " + result + " bytes");
            if(response != null)
            {
                if(mHelper == null){
                    mHelper = new MySqliteHelper(mContext);
                }
                SQLiteDatabase db =  mHelper.getWritableDatabase();  //创建数据库
                db.close();
                mDownloadFilesCount = 0;
                try {
                    String video_info_str = response.getString("data");
                    JSONArray video_info = new JSONArray(video_info_str);
                    mParserFromJson.clear();
                    for (int i = 0; i < video_info.length(); i++){
                        JSONObject video = video_info.getJSONObject(i);
                        String id = video.getString("id");
                        String name = video.getString("name");
                        String remote_url = video.getString("url");
                        Log.i(TAG, "id ="+id);
                        Log.i(TAG, "name ="+name);
                        Log.i(TAG, "url ="+remote_url);
                        String fileName = remote_url.substring(remote_url.lastIndexOf("/") + 1);
                        File file_to_check = new File(Environment.getExternalStorageDirectory() + DownloadFolder+fileName);
                        mParserFromJson.add(Environment.getExternalStorageDirectory() + DownloadFolder+fileName);
                        if(!file_to_check.exists()){
                            if(i==0){
                                Toast.makeText(mContext, R.string.str_start_downloading, Toast.LENGTH_SHORT).show();
                            }
                            mDownloadFilesCount++;
                            mTheFirstShowToast = true;
                            try {
                                URL downlaod_url = new URL(remote_url);
                                new DownloadFilesTask().execute(downlaod_url);
                            }catch(MalformedURLException ex){
                                Log.e(TAG,"URL parsing error");
                                ex.printStackTrace();
                            }
                        }
                    }
                    Log.e(TAG, "Success: " + video_info );
                    if(mDownloadFilesCount == 0){
                        if(mTheFirstShowToast == true) {
                            Toast.makeText(mContext, R.string.str_updated_and_no_download, Toast.LENGTH_SHORT).show();
                            mTheFirstShowToast = false;
                        }
                        checkDeleteFiles();
                    }

                } catch (JSONException ex) {
                    Log.e(TAG, "Failure", ex);
                }
            }
        }

    }

    public void checkDeleteFiles(){
        int filesNumberDeleted = 0;
        if(mFileList_1.size() > 0){
            for (int i =0; i< mFileList_1.size(); i++){
                String filePathToCheck = mFileList_1.get(i).toString();
                Log.i(TAG,"filePathToCheck ="+filePathToCheck);
                Log.i(TAG,"mParserFromJson =");
                if(i < mParserFromJson.size())
                    Log.i(TAG, mParserFromJson.get(i));
                if(!mParserFromJson.contains(filePathToCheck) && filePathToCheck.startsWith("/storage/emulated/0/MultiPlayer/") ){
//                    Toast.makeText(mContext, "Json from website does not contains"+filePathToCheck, Toast.LENGTH_LONG).show();
//                    mFileList_1.remove(i);
                    File fileToDelete = new File(mFileList_1.get(i));
                    fileToDelete.delete();
                    filesNumberDeleted ++;
                }
            }
            if(filesNumberDeleted > 0) {
                checkForUpdateResources();
            }
        }
    }

    public void setPlayMode(String play_mode){
        if(play_mode.equals("0")){
            play_mode = "All";
        }else if(play_mode.equals("1")){
            play_mode = "Web";
        }else if(play_mode.equals("2")){
            play_mode = "Local";
        }
        mSettingsSP.edit().putString(ModeKey, play_mode).apply();
    }
}
