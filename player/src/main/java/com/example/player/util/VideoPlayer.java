package com.example.player.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.player.R;
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
import com.google.android.exoplayer2.source.TrackGroupArray;
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

import java.util.ArrayList;
import java.util.List;

public class VideoPlayer {

    private final String CLASS_NAME = VideoPlayer.class.getName();
    private static final String TAG = "VideoPlayer";
    private Context context;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private List<MediaSource> subtitleSourceList;
    private DefaultTrackSelector trackSelector;


    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;
    private Uri videoUri;
    private List<Uri> subtitleUriList;

    private ComponentListener componentListener;
    private ProgressBar progressBar;


    public VideoPlayer(PlayerView playerView, Context context, String videoPath, @Nullable List<String> subTitlePath) {
        this.playerView = playerView;
        this.context = context;
        parseStringToUri(videoPath, subTitlePath);

        this.trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
        componentListener = new ComponentListener();

        playWhenReady = false;
        currentWindow = 0;
        playbackPosition = 0;
    }

    private void parseStringToUri(String videoPath, @Nullable List<String> subTitlePath) {
        this.videoUri = Uri.parse(videoPath);
        if (subTitlePath != null) {
            for (int i = 0; i < subTitlePath.size(); i++) {
                this.subtitleUriList.set(i, Uri.parse(subTitlePath.get(i)));
            }
        }

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
        boolean hasSubtitle = false;
        MergingMediaSource mergedSource;

        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(
                context,
                100 * 1024 * 1024,
                5 * 1024 * 1024);

        if (subtitleUriList != null) {
            hasSubtitle = true;
            Format subtitleFormat = Format.createTextSampleFormat(
                    null, // An identifier for the track. May be null.
                    MimeTypes.APPLICATION_SUBRIP, // The mime type. Must be set correctly.
                    Format.NO_VALUE, // Selection flags for the track.
                    "en"); // The subtitle language. May be null.

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, CLASS_NAME), new DefaultBandwidthMeter());

            for (int i = 0; i < subtitleUriList.size(); i++) {
                subtitleSourceList.set(i,
                        new SingleSampleMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(subtitleUriList.get(i), subtitleFormat, C.TIME_UNSET));
            }

        }

        switch (type) {
            case C.TYPE_SS:

                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_SS>> " + C.TYPE_SS);
                mediaSource = new SsMediaSource
                        .Factory(cacheDataSourceFactory)
                        .createMediaSource(videoUri);
                if (hasSubtitle) {
                    mergedSource = new MergingMediaSource(mediaSource, subtitleSourceList.get(0));
                    return mergedSource;
                } else
                    return mediaSource;

            case C.TYPE_DASH:

                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_DASH>> " + C.TYPE_DASH);
                mediaSource = new DashMediaSource
                        .Factory(cacheDataSourceFactory)
                        .createMediaSource(videoUri);
                if (hasSubtitle) {
                    mergedSource = new MergingMediaSource(mediaSource, subtitleSourceList.get(0));
                    return mergedSource;
                } else
                    return mediaSource;

            case C.TYPE_HLS:

                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_HLS>> " + C.TYPE_HLS);
                mediaSource = new HlsMediaSource
                        .Factory(cacheDataSourceFactory)
                        .createMediaSource(videoUri);
                if (hasSubtitle) {
                    mergedSource = new MergingMediaSource(mediaSource, subtitleSourceList.get(0));
                    return mergedSource;
                } else
                    return mediaSource;

            case C.TYPE_OTHER:
                Log.d(TAG, "buildMediaSource() called with: type >> " + type + " C.TYPE_OTHER>> " + C.TYPE_OTHER);
                mediaSource = new ExtractorMediaSource
                        .Factory(cacheDataSourceFactory)
                        .createMediaSource(videoUri);
                if (hasSubtitle) {
                    mergedSource = new MergingMediaSource(mediaSource, subtitleSourceList.get(0));
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
     mute, unMute and update mute icon
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
     repeat toggle and update repeat icon
     ***********************************************************/
    public void setRepeatToggleModes(int repeatToggleModes) {
        if (player != null) {
//            componentListener.onRepeatModeChanged(repeatToggleModes);

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

    public void setQuality(Activity activity, CharSequence dialogTitle) {

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

                Log.d(TAG, "setQuality(): " +
                        " mappedTrackInfo >> " + mappedTrackInfo +
                        " rendererType >> " + rendererType +
                        " C.TRACK_TYPE_VIDEO >> " + C.TRACK_TYPE_VIDEO +
                        " C.TRACK_TYPE_AUDIO >> " + C.TRACK_TYPE_AUDIO);
            }

        }
    }

    /***********************************************************
     forward and backward
     ***********************************************************/
    public void seekToSelectedPosition(int playbackPosition) {

        player.seekTo(player.getCurrentPosition() + playbackPosition);

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
                        progressBar.setVisibility(View.GONE);
                    case Player.STATE_ENDED:
                        progressBar.setVisibility(View.GONE);
                }
            }
        }

    }

}
