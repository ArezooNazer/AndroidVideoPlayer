package com.example.player.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayer {


    private String CLASS_NAME = VideoPlayer.class.getName();
    private static final String TAG = "VideoPlayer";

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Context context;
    private MediaSource videoMediaSource, subtitleMediaSource;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;
    private Uri uri;

    private DefaultTrackSelector trackSelector;
    private com.google.android.exoplayer2.ControlDispatcher controlDispatcher;


    public VideoPlayer(PlayerView playerView, Context context, String uri) {
        this.playerView = playerView;
        this.context = context;
        this.uri = Uri.parse(uri);
        this.controlDispatcher = new com.google.android.exoplayer2.DefaultControlDispatcher();
        this.trackSelector = new DefaultTrackSelector();
    }

    /******************************************************************
     initialize ExoPlayer
     ******************************************************************/

    public void initializePlayer() {
        if (player == null)
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        videoMediaSource = buildMediaSource(uri, null);

        playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        playerView.setKeepScreenOn(true);
        player.prepare(videoMediaSource);


    }

    /******************************************************************
     building mediaSource depend on stream type and caching
     ******************************************************************/

    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);

        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(
                context,
                100 * 1024 * 1024,
                5 * 1024 * 1024);

        switch (type) {
            case C.TYPE_SS:
                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_SS>> " + C.TYPE_SS);
                return new SsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri);

            case C.TYPE_DASH:
                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_DASH>> " + C.TYPE_DASH);
                return new DashMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri);

            case C.TYPE_HLS:
                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_HLS>> " + C.TYPE_HLS);
                return new HlsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri);

            case C.TYPE_OTHER:
                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_OTHER>> " + C.TYPE_OTHER);
                return new ExtractorMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    public void pausePlayer() {
        player.setPlayWhenReady(false);
    }

    public void resumePlayer() {
        player.setPlayWhenReady(true);
    }

    public void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            playerView.setPlayer(null);
            player.release();
            player = null;
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    /************************************************************
     mute, unMute and update mute icon
     ***********************************************************/
    public void setMute() {
        float currentVolume = player.getVolume();
        Log.d(TAG, "setMute() called" + currentVolume);
        if (currentVolume > 0)
            player.setVolume(0);
        else
            player.setVolume(1);
    }

    public void updateMuteIcon() {
    }

    /***********************************************************
     repeat toggle and update repeat icon
     ***********************************************************/
    public void setRepeatToggleModes() {

        if (player != null) {
            @Player.RepeatMode int currentMode = player.getRepeatMode();
            Log.d(TAG, "currentMode >>> [" + currentMode + "] ");
            switch (currentMode) {
                case Player.REPEAT_MODE_OFF:
                    controlDispatcher.dispatchSetRepeatMode(player, Player.REPEAT_MODE_ONE);
                    player.setRepeatMode(Player.REPEAT_MODE_ONE);

                case Player.REPEAT_MODE_ONE:
                    controlDispatcher.dispatchSetRepeatMode(player, Player.REPEAT_MODE_ALL);
                    player.setRepeatMode(Player.REPEAT_MODE_ALL);

                case Player.REPEAT_MODE_ALL:
                    controlDispatcher.dispatchSetRepeatMode(player, Player.REPEAT_MODE_OFF);
                    player.setRepeatMode(Player.REPEAT_MODE_OFF);
            }
        }
    }

    public void updateRepeatIcon() {
    }



}
