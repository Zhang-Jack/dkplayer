package com.angelfish.multiplayer.activity.extend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.angelfish.multiplayer.R;
import com.angelfish.multiplayer.widget.controller.PadController;
import com.angelfish.videoplayer.player.IjkVideoView;

public class PadActivity extends AppCompatActivity {

    private IjkVideoView mIjkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pad);
        mIjkVideoView = findViewById(R.id.video_view);

        mIjkVideoView.setUrl("http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv");

        mIjkVideoView.setVideoController(new PadController(this));

        mIjkVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIjkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIjkVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIjkVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!mIjkVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
