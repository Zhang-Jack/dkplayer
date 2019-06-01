package com.angelfish.multiplayer.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.angelfish.multiplayer.R;
import com.angelfish.videocontroller.StandardVideoController;
import com.angelfish.videoplayer.listener.OnVideoViewStateChangeListener;
import com.angelfish.videoplayer.player.IjkVideoView;
import com.angelfish.multiplayer.BuildConfig;
import com.google.android.exoplayer2.offline.Downloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class MainActivity extends AppCompatActivity{

    static final String TAG = "MultiPlayer";
//    private static final String VOD_URL = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
    private IjkVideoView mPlayer1;
    private IjkVideoView mPlayer2;
    private IjkVideoView mPlayer3;
    private IjkVideoView mPlayer4;
    private IjkVideoView mPlayer5;
    static private int backpressed = 0;
    private Context mContext;
    private boolean mIsPaused = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        isWriteStoragePermissionGranted();
        isReadStoragePermissionGranted();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_multi_player);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.hide();
        }
        String VOD_URL_1 = "android.resource://" + getPackageName() + "/" + R.raw.movie;
        mPlayer1 = findViewById(R.id.player_1);
        mPlayer1.setUrl(VOD_URL_1);
        String VOD_URL_2 = "android.resource://" + getPackageName() + "/" + R.raw.movie2;
        mPlayer2 = findViewById(R.id.player_2);
        mPlayer2.setUrl(VOD_URL_2);
        String VOD_URL_3 = "android.resource://" + getPackageName() + "/" + R.raw.movie3;
        mPlayer3 = findViewById(R.id.player_3);
        mPlayer3.setUrl(VOD_URL_3);
        String VOD_URL_4 = "android.resource://" + getPackageName() + "/" + R.raw.movie4;
        mPlayer4 = findViewById(R.id.player_4);
        mPlayer4.setUrl(VOD_URL_4);
        String VOD_URL_5 = "android.resource://" + getPackageName() + "/" + R.raw.movie5;
        mPlayer5 = findViewById(R.id.player_5);
        mPlayer5.setUrl(VOD_URL_5);
        checkForUpdateAds();
        startPlayingVideo();

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
        }
    }

    public void startPlayingVideo(){
        mPlayer1.setEnableAudioFocus(false);
        mPlayer1.setUsingSurfaceView(true);
//        StandardVideoController controller1 = new StandardVideoController(this);
//        mPlayer1.setVideoController(controller1);
        //高级设置（可选，须在start()之前调用方可生效）
        mPlayer1.setLooping(true);


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
                        startPlayingVideo2();
                        break;
                    case IjkVideoView.STATE_PAUSED:
                        break;
                    case IjkVideoView.STATE_BUFFERING:
                        break;
                    case IjkVideoView.STATE_BUFFERED:
                        break;
                    case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                        break;
                    case IjkVideoView.STATE_ERROR:
                        break;
                }
            }
        });

        mPlayer1.start();
        mPlayer1.setVisibility(View.GONE);
    }
    public void startPlayingVideo2(){
        mPlayer2.setEnableAudioFocus(false);
        mPlayer2.setUsingSurfaceView(true);
        //        StandardVideoController controller2 = new StandardVideoController(this);
        //        mPlayer2.setVideoController(controller2);
        mPlayer2.setLooping(true);
        //        mPlayer2.setMute(true);
        //        mPlayer2.start();
        mPlayer2.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
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
                        startPlayingVideo3();
                        break;
                    case IjkVideoView.STATE_PAUSED:
                        break;
                    case IjkVideoView.STATE_BUFFERING:
                        break;
                    case IjkVideoView.STATE_BUFFERED:
                        break;
                    case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                        break;
                    case IjkVideoView.STATE_ERROR:
                        break;
                }
            }
        });

        mPlayer2.start();
        mPlayer2.setVisibility(View.GONE);
    }

    public void startPlayingVideo3(){
        mPlayer3.setEnableAudioFocus(false);
        mPlayer3.setUsingSurfaceView(true);
        //        StandardVideoController controller2 = new StandardVideoController(this);
        //        mPlayer2.setVideoController(controller2);
        mPlayer3.setLooping(true);
        //        mPlayer2.setMute(true);
        //        mPlayer2.start();
        mPlayer3.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
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
                        startPlayingVideo4();
                        break;
                    case IjkVideoView.STATE_PAUSED:
                        break;
                    case IjkVideoView.STATE_BUFFERING:
                        break;
                    case IjkVideoView.STATE_BUFFERED:
                        break;
                    case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                        break;
                    case IjkVideoView.STATE_ERROR:
                        break;
                }
            }
        });

        mPlayer3.start();
        mPlayer3.setVisibility(View.GONE);
    }

    public void startPlayingVideo4(){
        mPlayer4.setEnableAudioFocus(false);
        mPlayer4.setUsingSurfaceView(true);
        //        StandardVideoController controller2 = new StandardVideoController(this);
        //        mPlayer2.setVideoController(controller2);
        mPlayer4.setLooping(true);
        //        mPlayer2.setMute(true);
        //        mPlayer2.start();
        mPlayer4.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
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
                        startPlayingVideo5();
                        break;
                    case IjkVideoView.STATE_PAUSED:
                        break;
                    case IjkVideoView.STATE_BUFFERING:
                        break;
                    case IjkVideoView.STATE_BUFFERED:
                        break;
                    case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                        break;
                    case IjkVideoView.STATE_ERROR:
                        break;
                }
            }
        });

        mPlayer4.start();
        mPlayer4.setVisibility(View.GONE);
    }

    public void startPlayingVideo5(){
        mPlayer5.setEnableAudioFocus(false);
        mPlayer5.setUsingSurfaceView(true);
        //        StandardVideoController controller2 = new StandardVideoController(this);
        //        mPlayer2.setVideoController(controller2);
        mPlayer5.setLooping(true);
        //        mPlayer2.setMute(true);
        //        mPlayer2.start();
        mPlayer5.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
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
                        mPlayer2.setVisibility(View.VISIBLE);
                        mPlayer3.setVisibility(View.VISIBLE);
                        mPlayer4.setVisibility(View.VISIBLE);
                        mPlayer5.setVisibility(View.VISIBLE);
                        break;
                    case IjkVideoView.STATE_PAUSED:
                        break;
                    case IjkVideoView.STATE_BUFFERING:
                        break;
                    case IjkVideoView.STATE_BUFFERED:
                        break;
                    case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                        break;
                    case IjkVideoView.STATE_ERROR:
                        break;
                }
            }
        });

        mPlayer5.start();
        mPlayer5.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer1.pause();
        mPlayer2.pause();
        mPlayer3.pause();
        mPlayer4.pause();
        mPlayer5.pause();
        mIsPaused = true;
    }

    @Override
    protected void onResume() {
        if(mIsPaused){
            mPlayer1.release();
            mPlayer2.release();
            mPlayer3.release();
            mPlayer4.release();
            mPlayer5.release();
            startPlayingVideo();
            mIsPaused = false;
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer1.release();
        mPlayer2.release();
        mPlayer3.release();
        mPlayer4.release();
        mPlayer5.release();
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
            PopupMenu popup = new PopupMenu(MainActivity.this, mPlayer2);
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
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
                    Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
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
        try{
        URL test_link = new URL("http://projector.auong.com/i/r/201906011836258919.mp4");
        new DownloadFilesTask().execute(test_link);
        }catch(MalformedURLException ex){
            ex.printStackTrace();
        }
    }

    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalSize = 0;
            try{
                for (int i = 0; i < count; i++) {
                    URLConnection conexion = urls[i].openConnection();
                    conexion.connect();

                    int lenghtOfFile = conexion.getContentLength();
                    Log.d(TAG, "Lenght of file: " + lenghtOfFile);
                    totalSize += lenghtOfFile;

                    InputStream input = new BufferedInputStream(conexion.getInputStream());
                    OutputStream output = new FileOutputStream("/sdcard/temp.mp4");
                    Log.d(TAG, "save to temp ");
                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        Log.d(TAG, "downlaod bytes: " + total);
                        publishProgress((int)((total*100)/lenghtOfFile));
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                }
            }catch(Exception e){
                return null;
            }
            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
//            showDialog("Downloaded " + result + " bytes");
        }
    }


}
