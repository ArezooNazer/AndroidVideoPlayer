package com.user.exoplayer.player.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.user.exoplayer.player.data.VideoSource;
import com.user.exoplayer.player.data.database.Subtitle;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
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
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;


public class VideoPlayer {

    private final String CLASS_NAME = VideoPlayer.class.getName();
    private static final String TAG = "VideoPlayer";
    private Context context;
    private PlayerController playerController;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private int widthOfScreen, index;
    private ComponentListener componentListener;
    private CacheDataSourceFactory cacheDataSourceFactory;
    private VideoSource videoSource;
    private boolean isLock = false;

    public VideoPlayer(PlayerView playerView,
                       Context context,
                       VideoSource videoSource,
                       PlayerController mView) {

        this.playerView = playerView;
        this.context = context;
        this.playerController = mView;
        this.cacheDataSourceFactory = new CacheDataSourceFactory(
                context,
                100 * 1024 * 1024,
                5 * 1024 * 1024);
        this.videoSource = videoSource;
        this.index = videoSource.getSelectedSourceIndex();
        this.trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
        if (componentListener == null)
            componentListener = new ComponentListener();

        initializePlayer();

    }

    /******************************************************************
     initialize ExoPlayer
     ******************************************************************/
    private void initializePlayer() {
        playerView.requestFocus();

        if (player == null)
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);

        player.setPlayWhenReady(true);
        player.addListener(componentListener);

        mediaSource = buildMediaSource(videoSource.getVideos().get(index), cacheDataSourceFactory);
        player.prepare(mediaSource);

    }

    /******************************************************************
     building mediaSource depend on stream type and caching
     ******************************************************************/
    private MediaSource buildMediaSource(VideoSource.SingleVideo singleVideo, CacheDataSourceFactory cacheDataSourceFactory) {
        Uri source = Uri.parse(singleVideo.getUrl());
        @C.ContentType int type = Util.inferContentType(source);
        switch (type) {
            case C.TYPE_SS:
                Log.d(TAG, "buildMediaSource() C.TYPE_SS = [" + C.TYPE_SS + "]");
                return new SsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(source);

            case C.TYPE_DASH:
                Log.d(TAG, "buildMediaSource() C.TYPE_DASH = [" + C.TYPE_DASH + "]");
                return new DashMediaSource.Factory(cacheDataSourceFactory).createMediaSource(source);

            case C.TYPE_HLS:
                Log.d(TAG, "buildMediaSource() C.TYPE_HLS = [" + C.TYPE_HLS + "]");
                return new HlsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(source);

            case C.TYPE_OTHER:
                Log.d(TAG, "buildMediaSource() C.TYPE_OTHER = [" + C.TYPE_OTHER + "]");
                return new ExtractorMediaSource.Factory(cacheDataSourceFactory).createMediaSource(source);

            default: {
                throw new IllegalStateException("Unsupported type: " + source);
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
            playerView.setPlayer(null);
            player.release();
            player.removeListener(componentListener);
            player = null;
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public VideoSource.SingleVideo getCurrentVideo() {
        return videoSource.getVideos().get(index);
    }

    /************************************************************
     mute, unMute
     ***********************************************************/
    public void setMute(boolean mute) {
        float currentVolume = player.getVolume();
        if (currentVolume > 0 && mute) {
            player.setVolume(0);
            playerController.setMuteMode(true);
        } else if (!mute && currentVolume == 0) {
            player.setVolume(1);
            playerController.setMuteMode(false);
        }
    }

    /***********************************************************
     manually select stream quality
     ***********************************************************/
    public void setSelectedQuality(Activity activity) {

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


                Pair<AlertDialog, MyTrackSelectionView> dialogPair =
                        MyTrackSelectionView.getDialog(activity, trackSelector,
                                rendererIndex,
                                player.getVideoFormat().bitrate);
                dialogPair.second.setShowDisableOption(false);
                dialogPair.second.setAllowAdaptiveSelections(allowAdaptiveSelections);
                dialogPair.second.animate();
                Log.d(TAG, "dialogPair.first.getListView()" + dialogPair.first.getListView());
                dialogPair.first.show();

            }

        }
    }

    /***********************************************************
     double tap event and seekTo
     ***********************************************************/
    public void seekToSelectedPosition(int hour, int minute, int second) {
        long playbackPosition = (hour * 3600 + minute * 60 + second) * 1000;
        player.seekTo(playbackPosition);
    }

    public void seekToSelectedPosition(long millisecond, boolean rewind) {
        if (rewind) {
            player.seekTo(player.getCurrentPosition() - 15000);
            return;
        }
        player.seekTo(millisecond * 1000);
    }

    public void seekToOnDoubleTap() {
        getWidthOfScreen();
        final GestureDetector gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        float positionOfDoubleTapX = e.getX();

                        if (positionOfDoubleTapX < widthOfScreen / 2)
                            player.seekTo(player.getCurrentPosition() - 5000);
                        else
                            player.seekTo(player.getCurrentPosition() + 5000);

                        Log.d(TAG, "onDoubleTap(): widthOfScreen >> " + widthOfScreen +
                                " positionOfDoubleTapX >>" + positionOfDoubleTapX);
                        return true;
                    }
                });

        playerView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void getWidthOfScreen() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        widthOfScreen = metrics.widthPixels;
    }

    /***********************************************************
     manually select subtitle
     ***********************************************************/
    void setSelectedSubtitle(Subtitle subtitle) {

        if (TextUtils.isEmpty(subtitle.getTitle()))
            Log.d(TAG, "setSelectedSubtitle: subtitle title is empty");


        Format subtitleFormat;
        subtitleFormat = Format.createTextSampleFormat(
                null,
                MimeTypes.APPLICATION_SUBRIP,
                Format.NO_VALUE,
                null);


        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, CLASS_NAME), new DefaultBandwidthMeter());

        MediaSource subtitleSource = new SingleSampleMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(subtitle.getSubtitleUrl()), subtitleFormat, C.TIME_UNSET);


        //optional
        playerController.changeSubtitleBackground();

        player.prepare(new MergingMediaSource(mediaSource, subtitleSource), false, false);
        playerController.showSubtitle(true);
        resumePlayer();


    }

    /***********************************************************
     playerView listener for lock and unlock screen
     ***********************************************************/
    public void lockScreen(boolean isLock) {
        this.isLock = isLock;
    }

    public boolean isLock() {
        return isLock;
    }

    /***********************************************************
     Listeners
     ***********************************************************/
    private class ComponentListener implements EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.d(TAG, "onPlayerStateChanged: playWhenReady: " + playWhenReady + " playbackState: " + playbackState);
            switch (playbackState) {
                case Player.STATE_IDLE:
                    playerController.showProgressBar(false);
                    playerController.showRetryBtn(true);
                    break;
                case Player.STATE_BUFFERING:
                    playerController.showProgressBar(true);
                    break;
                case Player.STATE_READY:
                    playerController.showProgressBar(false);
                    playerController.audioFocus();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            playerController.showProgressBar(false);
            playerController.showRetryBtn(true);
        }
    }

}