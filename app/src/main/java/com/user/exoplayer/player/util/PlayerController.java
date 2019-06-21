package com.user.exoplayer.player.util;

public interface PlayerController {

    void setMuteMode(boolean mute);
    void showProgressBar(boolean visible);
    void showRetryBtn(boolean visible);
    void showSubtitle(boolean show);
    void changeSubtitleBackground();
    void audioFocus();
    void setVideoWatchedLength();
    void videoEnded();
}
