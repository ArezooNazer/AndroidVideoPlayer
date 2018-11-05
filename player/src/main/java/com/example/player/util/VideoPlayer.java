package com.example.player.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
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
import com.google.android.exoplayer2.ui.MyTrackSelectionView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class VideoPlayer {

    private final String CLASS_NAME = VideoPlayer.class.getName();
    private static final String TAG = "VideoPlayer";
    private Context context;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private MediaSource mediaSource, subtitleSource;
    private DefaultTrackSelector trackSelector;

    private boolean playWhenReady;
    private int currentWindow, widthOfScreen;
    private long playbackPosition;

    private Uri videoUri, subtitleUri;
    private List<Uri> videoUriList;
    private String videoUrl;
    private ComponentListener componentListener;
    private ProgressBar progressBar;

    public VideoPlayer(PlayerView playerView, Context context, String videoPath) {
        this.playerView = playerView;
        this.context = context;
        this.videoUri = Uri.parse(videoPath);
        this.videoUrl = videoPath;

        this.trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
        if (componentListener == null)
            componentListener = new ComponentListener();

        playWhenReady = false;
        currentWindow = 0;
        playbackPosition = 0;

        initializePlayer();

    }


    public VideoPlayer(PlayerView playerView, Context context, List<String> videoPathList) {
        this.playerView = playerView;
        this.context = context;
        this.videoUriList = new ArrayList<>();

        for (int i = 0; i < videoPathList.size(); i++) {
            Log.e(TAG, "VideoPlayer: " + videoPathList.get(i) + " " + videoUriList);
            videoUriList.add(Uri.parse(videoPathList.get(i)));
        }

        this.trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
        if (componentListener == null)
            componentListener = new ComponentListener();

        playWhenReady = false;
        currentWindow = 0;
        playbackPosition = 0;

        initializePlayer();

    }

    /******************************************************************
     initialize ExoPlayer
     ******************************************************************/
    public void initializePlayer() {
        playerView.requestFocus();
        List<MediaSource> mediaSourceList = new ArrayList<>();
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(
                context,
                100 * 1024 * 1024,
                5 * 1024 * 1024);

        if (player == null)
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);


        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);

        player.setPlayWhenReady(true);
        player.addListener(componentListener);
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);


        if (videoUri != null) {
            mediaSource = buildMediaSource(videoUri, cacheDataSourceFactory);
            player.prepare(mediaSource);
        } else if (videoUriList != null) {
            for (int i = 0; i < videoUriList.size(); i++) {
                mediaSourceList.add(buildMediaSource(videoUriList.get(i), cacheDataSourceFactory));
                concatenatingMediaSource.addMediaSource(mediaSourceList.get(i));
            }
            this.mediaSource = concatenatingMediaSource;
            player.prepare(concatenatingMediaSource);
        }
    }

    /******************************************************************
     building mediaSource depend on stream type and caching
     ******************************************************************/
    private MediaSource buildMediaSource(Uri videoUri, CacheDataSourceFactory cacheDataSourceFactory) {
        @C.ContentType int type = Util.inferContentType(videoUri);

        switch (type) {
            case C.TYPE_SS:
                Log.d(TAG, "buildMediaSource() C.TYPE_SS = [" + C.TYPE_SS + "]");
                return new SsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(videoUri);

            case C.TYPE_DASH:
                Log.d(TAG, "buildMediaSource() C.TYPE_SS = [" + C.TYPE_DASH + "]");
                return new DashMediaSource.Factory(cacheDataSourceFactory).createMediaSource(videoUri);

            case C.TYPE_HLS:
                Log.d(TAG, "buildMediaSource() C.TYPE_SS = [" + C.TYPE_HLS + "]");
                return new HlsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(videoUri);

            case C.TYPE_OTHER:
                Log.d(TAG, "buildMediaSource() C.TYPE_SS = [" + C.TYPE_OTHER + "]");
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
     manually select stream quality
     ***********************************************************/
    public void setSelectedQuality(Activity activity, CharSequence dialogTitle) {

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
                        MyTrackSelectionView.getDialog(activity, dialogTitle, trackSelector, rendererIndex);
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

//        long videoDuration = getVideoDuration();
//        if (player != null) {
//            if (playbackPosition <= videoDuration) {
//                player.seekTo(playbackPosition);
//            } else {
//                Toast.makeText(context, "playbackPosition <= mTimeInMilliseconds", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    private long getVideoDuration() {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoUrl, new HashMap<>());
        String mVideoDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        Log.d(TAG, "videoDuration >>  " + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        Log.d(TAG, "ARTIST >>  " + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        Log.d(TAG, "TITLE >>  " + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        Log.d(TAG, "BITRATE >>  " + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));

        return Long.parseLong(mVideoDuration);
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
    public void setSelectedSubtitle(String subtitle) {
        MergingMediaSource mergedSource;
        if (subtitle != null) {
            this.subtitleUri = Uri.parse(subtitle);

            Format subtitleFormat = Format.createTextSampleFormat(
                    null, // An identifier for the track. May be null.
                    MimeTypes.APPLICATION_SUBRIP, // The mime type. Must be set correctly.
                    Format.NO_VALUE, // Selection flags for the track.
                    null); // The subtitle language. May be null.

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, CLASS_NAME), new DefaultBandwidthMeter());


            subtitleSource = new SingleSampleMediaSource
                    .Factory(dataSourceFactory)
                    .createMediaSource(subtitleUri, subtitleFormat, C.TIME_UNSET);


            mergedSource = new MergingMediaSource(mediaSource, subtitleSource);
            player.prepare(mergedSource, false, false);
            resumePlayer();

        } else {
            Toast.makeText(context, "there is no subtitle", Toast.LENGTH_SHORT).show();
        }
    }


    /***********************************************************
     playerView listener for lock and unlock screen
     ***********************************************************/
    public void setPlayerViewListener(boolean isLock) {
        playerView.setControllerVisibilityListener(visibility -> {
            if (isLock)
                playerView.hideController();
            else
                playerView.showController();
        });
    }

    /***********************************************************
     Listeners
     ***********************************************************/
    private class ComponentListener implements EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            if (progressBar != null) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

}
