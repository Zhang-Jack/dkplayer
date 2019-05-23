package com.angelfish.multiplayer.util;

import com.angelfish.multiplayer.R;
import com.angelfish.multiplayer.app.MyApplication;
import com.dueeeke.videoplayer.player.IjkVideoView;

/**
 * 无缝播放
 */

public class SeamlessPlayHelper {

    private IjkVideoView mIjkVideoView;
    private static SeamlessPlayHelper instance;

    private SeamlessPlayHelper() {
        mIjkVideoView = new IjkVideoView(MyApplication.getInstance());
        mIjkVideoView.setId(R.id.video_player);
    }

    public static SeamlessPlayHelper getInstance() {
        if (instance == null) {
            synchronized (SeamlessPlayHelper.class) {
                if (instance == null) {
                    instance = new SeamlessPlayHelper();
                }
            }
        }
        return instance;
    }


    public IjkVideoView getIjkVideoView() {
        return mIjkVideoView;
    }




}
