package com.angelfish.multiplayer.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.angelfish.multiplayer.R;
import com.angelfish.videocontroller.StandardVideoController;
import com.angelfish.videoplayer.player.IjkVideoView;

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
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String VOD_URL = "android.resource://" + getPackageName() + "/" + R.raw.movie;

        mPlayer1 = findViewById(R.id.player_1);
        mPlayer1.setUrl(VOD_URL);

        mPlayer1.setEnableAudioFocus(false);
        StandardVideoController controller1 = new StandardVideoController(this);
        mPlayer1.setVideoController(controller1);

        mPlayer2 = findViewById(R.id.player_2);
        mPlayer2.setUrl(VOD_URL);
        mPlayer2.setEnableAudioFocus(false);
        StandardVideoController controller2 = new StandardVideoController(this);
        mPlayer2.setVideoController(controller2);

        mPlayer3 = findViewById(R.id.player_3);
        mPlayer3.setUrl(VOD_URL);

        mPlayer3.setEnableAudioFocus(false);
        StandardVideoController controller3 = new StandardVideoController(this);
        mPlayer3.setVideoController(controller3);

        mPlayer4 = findViewById(R.id.player_4);
        mPlayer4.setUrl(VOD_URL);

        mPlayer4.setEnableAudioFocus(false);
        StandardVideoController controller4 = new StandardVideoController(this);
        mPlayer4.setVideoController(controller4);

        mPlayer5 = findViewById(R.id.player_5);
        mPlayer5.setUrl(VOD_URL);

        mPlayer5.setEnableAudioFocus(false);
        StandardVideoController controller5 = new StandardVideoController(this);
        mPlayer5.setVideoController(controller5);
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
        mPlayer1.resume();
        mPlayer2.resume();
        mPlayer3.pause();
        mPlayer4.pause();
        mPlayer5.pause();
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
