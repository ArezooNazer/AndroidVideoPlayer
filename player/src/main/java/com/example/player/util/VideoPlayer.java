package com.example.player.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import com.example.player.R;
import com.example.player.ui.PlayerActivity;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import javax.sql.DataSource;

public class VideoPlayer {


    private String CLASS_NAME = VideoPlayer.class.getName();
    private static final String TAG = "VideoPlayer";

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Context context;
    private MediaSource mediaSource, subtitleSource;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;
    private Uri uri, subtitleUri;
    private MergingMediaSource mergedSource;

    private DefaultTrackSelector trackSelector;
    private com.google.android.exoplayer2.ControlDispatcher controlDispatcher;


    public VideoPlayer(PlayerView playerView, Context context, String videoUri, @Nullable String subTitleUri) {
        this.playerView = playerView;
        this.context = context;
        this.uri = Uri.parse(videoUri);
        if (subTitleUri != null)
            this.subtitleUri = Uri.parse(subTitleUri);
        this.controlDispatcher = new com.google.android.exoplayer2.DefaultControlDispatcher();
        this.trackSelector = new DefaultTrackSelector();
    }

    /******************************************************************
     initialize ExoPlayer
     ******************************************************************/

    public void initializePlayer() {
        if (player == null)
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        mediaSource = buildMediaSource(uri, subtitleUri, null);

        playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        playerView.setKeepScreenOn(true);
        player.prepare(mediaSource);

//        setQuality(trackSelector);

    }

    /******************************************************************
     building mediaSource depend on stream type and caching
     ******************************************************************/

    private MediaSource buildMediaSource(Uri uri, Uri subtitleUri, @Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        boolean hasSubtitle = false;

        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(
                context,
                100 * 1024 * 1024,
                5 * 1024 * 1024);

        // Build the subtitle MediaSource.
        Format subtitleFormat = Format.createTextSampleFormat(
                null, // An identifier for the track. May be null.
                MimeTypes.APPLICATION_SUBRIP, // The mime type. Must be set correctly.
                Format.NO_VALUE, // Selection flags for the track.
                "fa"); // The subtitle language. May be null.

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, CLASS_NAME), new DefaultBandwidthMeter());

        if (subtitleUri != null) {
            subtitleSource = new SingleSampleMediaSource
                    .Factory(dataSourceFactory)
                    .createMediaSource(subtitleUri, subtitleFormat, C.TIME_UNSET);
            hasSubtitle = true;
        }

        switch (type) {
            case C.TYPE_SS:

                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_SS>> " + C.TYPE_SS);
                mediaSource = new SsMediaSource
                        .Factory(cacheDataSourceFactory)
                        .createMediaSource(uri);
                if (hasSubtitle) {
                    mergedSource = new MergingMediaSource(mediaSource, subtitleSource);
                    return mergedSource;
                } else
                    return mediaSource;

            case C.TYPE_DASH:

                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_DASH>> " + C.TYPE_DASH);
                mediaSource = new DashMediaSource
                        .Factory(cacheDataSourceFactory)
                        .createMediaSource(uri);
                if (hasSubtitle) {
                    mergedSource = new MergingMediaSource(mediaSource, subtitleSource);
                    return mergedSource;
                } else
                    return mediaSource;

            case C.TYPE_HLS:

                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_HLS>> " + C.TYPE_HLS);
                mediaSource = new HlsMediaSource
                        .Factory(cacheDataSourceFactory)
                        .createMediaSource(uri);
                if (hasSubtitle) {
                    mergedSource = new MergingMediaSource(mediaSource, subtitleSource);
                    return mergedSource;
                } else
                    return mediaSource;

//                return new HlsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri);

            case C.TYPE_OTHER:
                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_OTHER>> " + C.TYPE_OTHER);
                mediaSource = new ExtractorMediaSource
                        .Factory(cacheDataSourceFactory)
                        .createMediaSource(uri);
                if (hasSubtitle) {
                    mergedSource = new MergingMediaSource(mediaSource, subtitleSource);
                    return mergedSource;
                } else
                    return mediaSource;

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

    /***********************************************************
     manually select stream quality
     ***********************************************************/

    public void setQuality(DefaultTrackSelector trackSelector) {

        int videoRendererIndex;
        TrackGroupArray trackGroups;
        DefaultTrackSelector trackSelector1 = trackSelector;
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo =
                trackSelector.getCurrentMappedTrackInfo();
        Log.d(TAG, "trackSelector>> " + trackSelector + "mappedTrackInfo>>>  " + mappedTrackInfo);

        if (mappedTrackInfo != null) {

            for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
                trackGroups = mappedTrackInfo.getTrackGroups(i);

                if (trackGroups.length != 0) {
                    switch (player.getRendererType(i)) {
                        case C.TRACK_TYPE_VIDEO:
                            videoRendererIndex = i;
//                        return true;
                    }
                }
            }
        }
    }

    /***********************************************************
     manually select subtitle
     ***********************************************************/
    public void setSubtitle(Button button) {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo =
                trackSelector.getCurrentMappedTrackInfo();

//         trackSelectionHelper = new TrackSelectionHelper(trackSelector, adaptiveTrackSelectionFactory);

        if (mappedTrackInfo != null) {
            for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
                TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
                if (trackGroups.length != 0) {
                    int label;
                    switch (player.getRendererType(i)) {
                        case C.TRACK_TYPE_AUDIO:
                            label = R.string.audio;
                            break;
                        case C.TRACK_TYPE_VIDEO:
                            label = R.string.video;
                            break;
                        case C.TRACK_TYPE_TEXT:
                            label = R.string.text;
                            break;
                        default:
                            continue;
                    }
//                    button.setText(label);
//                    button.setTag(i);
//                    button.setOnClickListener(this);
//                    debugRootView.addView(button, debugRootView.getChildCount() - 1);
                }

            }
        }
    }
}
