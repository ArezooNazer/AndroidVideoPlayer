package com.example.player.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public class VideoPlayer {

    private final String CLASS_NAME = VideoPlayer.class.getName();
    private static final String TAG = "VideoPlayer";
    private Context context;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private MediaSource mediaSource, subtitleSource;
    private List<MediaSource> subtitleSourceList;
    private DefaultTrackSelector trackSelector;


    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;
    private Uri videoUri, subtitleUri;
    private ComponentListener componentListener;
    private ProgressBar progressBar;


    public VideoPlayer(PlayerView playerView, Context context, String videoPath) {
        this.playerView = playerView;
        this.context = context;
        this.videoUri = Uri.parse(videoPath);

        this.trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
        componentListener = new ComponentListener();

        playWhenReady = false;
        currentWindow = 0;
        playbackPosition = 0;
    }

    /******************************************************************
     initialize ExoPlayer
     ******************************************************************/
    public void initializePlayer() {
        playerView.requestFocus();


        if (player == null)
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        mediaSource = buildMediaSource(null);

        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);

        player.setPlayWhenReady(true);
        player.addListener(componentListener);
        player.prepare(mediaSource);

    }

    /******************************************************************
     building mediaSource depend on stream type and caching
     ******************************************************************/
    private MediaSource buildMediaSource(@Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(videoUri, overrideExtension);

        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(
                context,
                100 * 1024 * 1024,
                5 * 1024 * 1024);

        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(videoUri);

            case C.TYPE_DASH:
                return new DashMediaSource.Factory(cacheDataSourceFactory).createMediaSource(videoUri);

            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(videoUri);

            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(cacheDataSourceFactory).createMediaSource(videoUri);

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
            player.removeListener(componentListener);
            player = null;
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public ProgressBar setProgressbar(ProgressBar progressBar) {
        return this.progressBar = progressBar;
    }

    /************************************************************
     mute, unMute
     ***********************************************************/
    public void setMute(boolean mute) {
        float currentVolume = player.getVolume();
        Log.d(TAG, "setMute() called" + currentVolume);

        if (currentVolume > 0 && mute)
            player.setVolume(0);
        else if (!mute && currentVolume == 0)
            player.setVolume(1);
    }

    /***********************************************************
     repeat toggle
     ***********************************************************/
    public void setRepeatToggleModes(int repeatToggleModes) {
        if (player != null) {

            if (player != null) {

                if (repeatToggleModes == Player.REPEAT_MODE_OFF)
                    player.setRepeatMode(Player.REPEAT_MODE_ONE);

                if (repeatToggleModes == Player.REPEAT_MODE_ONE)
                    player.setRepeatMode(Player.REPEAT_MODE_ALL);

                if (repeatToggleModes == Player.REPEAT_MODE_ALL)
                    player.setRepeatMode(Player.REPEAT_MODE_OFF);
            }
        }
    }

    /***********************************************************
     manually select stream quality
     ***********************************************************/
    public void setSelectedQuality(Activity activity, CharSequence dialogTitle) {

        player.setPlayWhenReady(false);
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo;

        if (trackSelector != null) {
            mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();

            if (mappedTrackInfo != null) {

                int rendererIndex = 0; // renderer for video
                int rendererType = mappedTrackInfo.getRendererType(rendererIndex);
                boolean allowAdaptiveSelections =
                        rendererType == C.TRACK_TYPE_VIDEO
                                || (rendererType == C.TRACK_TYPE_AUDIO
                                && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                                == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS);


                Pair<AlertDialog, TrackSelectionView> dialogPair =
                        TrackSelectionView.getDialog(activity, dialogTitle, trackSelector, rendererIndex);
                dialogPair.second.setShowDisableOption(false);
                dialogPair.second.setAllowAdaptiveSelections(allowAdaptiveSelections);
                dialogPair.first.show();

                Log.d(TAG, "setSelectedQuality(): " +
                        " mappedTrackInfo >> " + mappedTrackInfo +
                        " rendererType >> " + rendererType +
                        " C.TRACK_TYPE_VIDEO >> " + C.TRACK_TYPE_VIDEO +
                        " C.TRACK_TYPE_AUDIO >> " + C.TRACK_TYPE_AUDIO);
            }

        }
        player.setPlayWhenReady(true);
    }

    /***********************************************************
     double tap event
     ***********************************************************/
//    public void seekToSelectedPosition(int playbackPosition , boolean isForward) {
//        long videoDuration = player.getDuration();
//
//        long upperBound = player.getCurrentPosition() + playbackPosition;
//        long lowerBound = player.getCurrentPosition() - playbackPosition;
//
//        if(isForward && upperBound < videoDuration )
//            player.seekTo(upperBound);
//        if(!isForward && lowerBound >0)
//            player.seekTo(lowerBound);
//
//    }
    public void seekToSelectedPosition(int hour, int minute, int second) {
        long playbackPosition = (hour * 3600 + minute * 60 + second) * 1000;
        long videoDuration = player.getDuration();

        Log.d(TAG, "seekToSelectedPosition() called with: playbackPosition = [" + playbackPosition + "], videoDuration = [" + videoDuration + "]");

        if (playbackPosition <= videoDuration) {
            player.seekTo(playbackPosition);
        }
    }

    public void seekForwardOnDoubleTap() {

        final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("tap", "onDoubleTap() called with: e = [" + e + "]");
                player.seekTo(player.getCurrentPosition() + 5000);
                return true;
            }
        });

        playerView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }


    /***********************************************************
     manually select subtitle
     ***********************************************************/
    public void setSelectedSubtitle(String subtitle) {

        MergingMediaSource mergedSource;

        if (subtitle != null) {
            this.subtitleUri = Uri.parse(subtitle);

            Format subtitleFormat = Format.createTextSampleFormat(
                    null, // An identifier for the track. May be null.
                    MimeTypes.APPLICATION_SUBRIP, // The mime type. Must be set correctly.
                    Format.NO_VALUE, // Selection flags for the track.
                    "en"); // The subtitle language. May be null.

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, CLASS_NAME), new DefaultBandwidthMeter());


            subtitleSource = new SingleSampleMediaSource
                    .Factory(dataSourceFactory)
                    .createMediaSource(subtitleUri, subtitleFormat, C.TIME_UNSET);


            mergedSource = new MergingMediaSource(mediaSource, subtitleSource);
            player.prepare(mergedSource, false, false);
            Toast.makeText(context, "there is subtitle", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "there is no subtitle", Toast.LENGTH_SHORT).show();
        }
    }


    /***********************************************************
     Listeners
     ***********************************************************/
    private class ComponentListener implements EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            if (progressBar != null) {

                switch (playbackState) {
                    case Player.STATE_IDLE:
                        progressBar.setVisibility(View.VISIBLE);

                    case Player.STATE_BUFFERING:
                        progressBar.setVisibility(View.VISIBLE);

                    case Player.STATE_READY:
                        progressBar.setVisibility(View.VISIBLE);

                    case Player.STATE_ENDED:
                        progressBar.setVisibility(View.GONE);

                }
            }
        }

    }

}
