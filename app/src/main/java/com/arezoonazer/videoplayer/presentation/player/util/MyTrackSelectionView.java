package com.arezoonazer.videoplayer.presentation.player.util;

/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//package com.google.android.exoplayer2.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider;
import com.google.android.exoplayer2.ui.TrackNameProvider;
import com.google.android.exoplayer2.util.Assertions;
import com.arezoonazer.videoplayer.R;

import java.util.Arrays;

/**
 * A view for making track selections.
 */
public class MyTrackSelectionView extends LinearLayout {

    private static final String TAG = "MyTrackSelectionView";

    private final int selectableItemBackgroundResourceId;
    private final LayoutInflater inflater;
    private final CheckedTextView disableView;
    private final CheckedTextView defaultView;
    private final ComponentListener componentListener;

    private boolean allowAdaptiveSelections;

    private TrackNameProvider trackNameProvider;
    private CheckedTextView[][] trackViews;

    private DefaultTrackSelector trackSelector;
    private int rendererIndex;
    private TrackGroupArray trackGroups;
    private boolean isDisabled;
    private @Nullable
    SelectionOverride override;
    private final String playingString = "<font color=#673AB7> &nbsp;(playing) &nbsp; </font>";
    private static long currentBitrate;

    private static final int BITRATE_1080P = 2800000;
    private static final int BITRATE_720P = 1600000;
    private static final int BITRATE_480P = 700000;
    private static final int BITRATE_360P = 530000;
    private static final int BITRATE_240P = 400000;
    private static final int BITRATE_160P = 300000;

    public static Pair<AlertDialog, MyTrackSelectionView> getDialog(
            Activity activity,
            DefaultTrackSelector trackSelector,
            int rendererIndex,
            long currentBitrate) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        MyTrackSelectionView.currentBitrate = currentBitrate;

        // Inflate with the builder's context to ensure the correct style is used.
        LayoutInflater dialogInflater = LayoutInflater.from(builder.getContext());
        View dialogView = dialogInflater.inflate(com.google.android.exoplayer2.ui.R.layout.exo_track_selection_dialog, null);

        final MyTrackSelectionView selectionView = dialogView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_track_selection_view);
        selectionView.init(trackSelector, rendererIndex);
        Dialog.OnClickListener okClickListener = (dialog, which) -> selectionView.applySelection();

        AlertDialog dialog =
                builder
                        .setView(dialogView)
                        .setPositiveButton(android.R.string.ok, okClickListener)
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();
        return Pair.create(dialog, selectionView);
    }

    public MyTrackSelectionView(Context context) {
        this(context, null);
    }

    public MyTrackSelectionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("nullness")
    public MyTrackSelectionView(
            Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributeArray =
                context
                        .getTheme()
                        .obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        selectableItemBackgroundResourceId = attributeArray.getResourceId(0, 0);
        Log.e(TAG, "MyTrackSelectionView: " + selectableItemBackgroundResourceId);
        attributeArray.recycle();


        inflater = LayoutInflater.from(context);
        componentListener = new ComponentListener();
        trackNameProvider = new DefaultTrackNameProvider(getResources());

        // View for disabling the renderer.
        disableView =
                (CheckedTextView)
                        inflater.inflate(android.R.layout.simple_list_item_single_choice, this, false);
        disableView.setBackgroundResource(selectableItemBackgroundResourceId);
        disableView.setText(R.string.exo_track_selection_none);
        disableView.setEnabled(false);
        disableView.setFocusable(true);
        disableView.setOnClickListener(componentListener);
        disableView.setVisibility(View.GONE);
        addView(disableView);
        // Divider view.
        addView(inflater.inflate(R.layout.exo_list_divider, this, false));
        // View for clearing the override to allow the selector to use its default selection logic.
        defaultView =
                (CheckedTextView)
                        inflater.inflate(android.R.layout.simple_list_item_single_choice, this, false);
        defaultView.setBackgroundResource(selectableItemBackgroundResourceId);
        defaultView.setText(R.string.exo_track_selection_auto);
        defaultView.setEnabled(false);
        defaultView.setFocusable(true);
        defaultView.setOnClickListener(componentListener);
        addView(defaultView);
    }

    /**
     * Sets whether adaptive selections (consisting of more than one track) can be made using this
     * selection view.
     *
     * <p>For the view to enable adaptive selection it is necessary both for this feature to be
     * enabled, and for the target renderer to support adaptation between the available tracks.
     *
     * @param allowAdaptiveSelections Whether adaptive selection is enabled.
     */
    public void setAllowAdaptiveSelections(boolean allowAdaptiveSelections) {
        if (this.allowAdaptiveSelections != allowAdaptiveSelections) {
            this.allowAdaptiveSelections = allowAdaptiveSelections;
            updateViews();
        }
    }

    /**
     * Sets whether an option is available for disabling the renderer.
     *
     * @param showDisableOption Whether the disable option is shown.
     */
    public void setShowDisableOption(boolean showDisableOption) {
        disableView.setVisibility(showDisableOption ? View.VISIBLE : View.GONE);
    }

    /**
     * Sets the {@link TrackNameProvider} used to generate the user visible name of each track and
     * updates the view with track names queried from the specified provider.
     *
     * @param trackNameProvider The {@link TrackNameProvider} to use.
     */
    public void setTrackNameProvider(TrackNameProvider trackNameProvider) {
        this.trackNameProvider = Assertions.checkNotNull(trackNameProvider);
        updateViews();
    }

    /**
     * Initialize the view to select tracks for a specified renderer using a {@link
     * DefaultTrackSelector}.
     *
     * @param trackSelector The {@link DefaultTrackSelector}.
     * @param rendererIndex The index of the renderer.
     */
    public void init(DefaultTrackSelector trackSelector, int rendererIndex) {
        this.trackSelector = trackSelector;
        this.rendererIndex = rendererIndex;
        updateViews();
    }

    // Private methods.

    private void updateViews() {
        // Remove previous per-track views.
        for (int i = getChildCount() - 1; i >= 3; i--) {
            removeViewAt(i);
        }

        MappingTrackSelector.MappedTrackInfo trackInfo =
                trackSelector == null ? null : trackSelector.getCurrentMappedTrackInfo();
        if (trackSelector == null || trackInfo == null) {
            // The view is not initialized.
            disableView.setEnabled(false);
            defaultView.setEnabled(false);
            return;
        }
        disableView.setEnabled(true);
        defaultView.setEnabled(true);

        trackGroups = trackInfo.getTrackGroups(rendererIndex);

        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        isDisabled = parameters.getRendererDisabled(rendererIndex);
        override = parameters.getSelectionOverride(rendererIndex, trackGroups);

        // Add per-track views.
        trackViews = new CheckedTextView[trackGroups.length][];
        for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
            TrackGroup group = trackGroups.get(groupIndex);
            boolean enableAdaptiveSelections =
                    allowAdaptiveSelections
                            && trackGroups.get(groupIndex).length > 1
                            && trackInfo.getAdaptiveSupport(rendererIndex, groupIndex, false)
                            != RendererCapabilities.ADAPTIVE_NOT_SUPPORTED;
            trackViews[groupIndex] = new CheckedTextView[group.length];
            for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                if (trackIndex == 0) {
                    addView(inflater.inflate(com.google.android.exoplayer2.ui.R.layout.exo_list_divider, this, false));
                }
                int trackViewLayoutId =
                        enableAdaptiveSelections
                                ? android.R.layout.simple_list_item_single_choice
                                : android.R.layout.simple_list_item_single_choice;
                CheckedTextView trackView =
                        (CheckedTextView) inflater.inflate(trackViewLayoutId, this, false);
                trackView.setBackgroundResource(selectableItemBackgroundResourceId);
                trackView.setText(Html.fromHtml(buildBitrateString(group.getFormat(trackIndex))));
                if (trackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex)
                        == RendererCapabilities.FORMAT_HANDLED) {
                    trackView.setFocusable(true);
                    trackView.setTag(Pair.create(groupIndex, trackIndex));
                    trackView.setOnClickListener(componentListener);
                } else {
                    trackView.setFocusable(false);
                    trackView.setEnabled(false);
                }
                trackViews[groupIndex][trackIndex] = trackView;
                addView(trackView);
            }
        }

        updateViewStates();
    }

    private void updateViewStates() {
        disableView.setChecked(isDisabled);
        defaultView.setChecked(!isDisabled && override == null);
        for (int i = 0; i < trackViews.length; i++) {
            for (int j = 0; j < trackViews[i].length; j++) {
                trackViews[i][j].setChecked(override != null && override.groupIndex == i && override.containsTrack(j));
//                Log.d(TAG, "override.groupIndex" + override.groupIndex + " override.containsTrack(j) " + override.containsTrack(j));
            }
        }
    }


    private void applySelection() {
        DefaultTrackSelector.ParametersBuilder parametersBuilder = trackSelector.buildUponParameters();
        parametersBuilder.setRendererDisabled(rendererIndex, isDisabled);
        if (override != null) {
            parametersBuilder.setSelectionOverride(rendererIndex, trackGroups, override);
        } else {
            parametersBuilder.clearSelectionOverrides(rendererIndex);
        }
        trackSelector.setParameters(parametersBuilder);
    }

    private void onClick(View view) {
        if (view == disableView) {
            onDisableViewClicked();
        } else if (view == defaultView) {
            onDefaultViewClicked();
        } else {
            onTrackViewClicked(view);
        }
        updateViewStates();
    }

    private void onDisableViewClicked() {
        isDisabled = true;
        override = null;
    }

    private void onDefaultViewClicked() {
        isDisabled = false;
        override = null;
    }

    private void onTrackViewClicked(View view) {
        isDisabled = false;
        @SuppressWarnings("unchecked")
        Pair<Integer, Integer> tag = (Pair<Integer, Integer>) view.getTag();
        int groupIndex = tag.first;
        int trackIndex = tag.second;

        if (override == null) {
            override = new SelectionOverride(groupIndex, trackIndex);

        } else {
            int[] overrideTracks = override.tracks;
            int[] tracks = getTracksRemoving(overrideTracks, override.tracks[0]);
            override = new SelectionOverride(groupIndex, tracks);
            override = new SelectionOverride(groupIndex, trackIndex);
        }
    }

    private static int[] getTracksAdding(int[] tracks, int addedTrack) {
        tracks = Arrays.copyOf(tracks, tracks.length + 1);
        tracks[tracks.length - 1] = addedTrack;
        return tracks;
    }

    private static int[] getTracksRemoving(int[] tracks, int removedTrack) {
        int[] newTracks = new int[tracks.length - 1];
        int trackCount = 0;
        for (int track : tracks) {
            if (track != removedTrack) {
                newTracks[trackCount++] = track;
            }
        }
        return newTracks;
    }

    // Internal classes.

    private class ComponentListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            MyTrackSelectionView.this.onClick(view);
        }
    }

    private String buildBitrateString(Format format) {
        int bitrate = format.bitrate;
        boolean isPlaying = currentBitrate == bitrate;

        if (bitrate == Format.NO_VALUE) {
            return updateText(isPlaying, trackNameProvider.getTrackName(format));
        }
        if (bitrate <= BITRATE_160P) {
            return updateText(isPlaying, " 160P");
        }
        if (bitrate <= BITRATE_240P) {
            return updateText(isPlaying, " 240P");
        }
        if (bitrate <= BITRATE_360P) {
            return updateText(isPlaying, " 360P");
        }
        if (bitrate <= BITRATE_480P) {
            return updateText(isPlaying, " 480P");
        }
        if (bitrate <= BITRATE_720P) {
            return updateText(isPlaying, " 720P");
        }
        if (bitrate <= BITRATE_1080P) {
            return updateText(isPlaying, " 1080P");
        }
        return trackNameProvider.getTrackName(format);
    }

    private String updateText(boolean isPlaying, String quality) {
        if (isPlaying) {
            if (!quality.contains(playingString))
                return quality + playingString;
            return quality;
        }

        return quality.replace(playingString, "");
    }
}
