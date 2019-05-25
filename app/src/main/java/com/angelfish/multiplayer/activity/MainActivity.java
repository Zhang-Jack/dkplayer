package com.angelfish.multiplayer.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.angelfish.multiplayer.R;
import com.angelfish.videocontroller.StandardVideoController;
import com.angelfish.videoplayer.listener.OnVideoViewStateChangeListener;
import com.angelfish.videoplayer.player.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class MainActivity extends AppCompatActivity{

//    private static final String VOD_URL = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
    private IjkVideoView mPlayer1;
    private IjkVideoView mPlayer2;
    private IjkVideoView mPlayer3;
    private IjkVideoView mPlayer4;
    private IjkVideoView mPlayer5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public void startPlayingVideo2(){
        String VOD_URL_2 = "android.resource://" + getPackageName() + "/" + R.raw.movie2;
        mPlayer2 = findViewById(R.id.player_2);
        mPlayer2.setUrl(VOD_URL_2);
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
    }

    public void startPlayingVideo3(){
        String VOD_URL_3 = "android.resource://" + getPackageName() + "/" + R.raw.movie3;
        mPlayer3 = findViewById(R.id.player_3);
        mPlayer3.setUrl(VOD_URL_3);
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
    }

    public void startPlayingVideo4(){
        String VOD_URL_4 = "android.resource://" + getPackageName() + "/" + R.raw.movie4;
        mPlayer4 = findViewById(R.id.player_4);
        mPlayer4.setUrl(VOD_URL_4);
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
    }

    public void startPlayingVideo5(){
        String VOD_URL_5 = "android.resource://" + getPackageName() + "/" + R.raw.movie5;
        mPlayer5 = findViewById(R.id.player_5);
        mPlayer5.setUrl(VOD_URL_5);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer1.pause();
        mPlayer2.pause();
        mPlayer3.pause();
        mPlayer4.pause();
        mPlayer5.pause();
    }

    @Override
    protected void onResume() {
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
}
