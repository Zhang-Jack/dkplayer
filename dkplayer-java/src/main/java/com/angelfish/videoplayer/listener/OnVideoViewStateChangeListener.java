package com.angelfish.videoplayer.listener;

public interface OnVideoViewStateChangeListener {
    void onPlayerStateChanged(int playerState);
    void onPlayStateChanged(int playState);
}