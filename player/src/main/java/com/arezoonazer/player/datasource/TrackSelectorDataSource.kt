package com.arezoonazer.player.datasource

import android.content.Context
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TrackSelectorDataSource @Inject constructor(
    @ApplicationContext val context: Context
) {

    private var _trackSelector: DefaultTrackSelector? =
        DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())
    val trackSelector: DefaultTrackSelector = requireNotNull(_trackSelector)

    fun nullifyTrackSelector() {
        _trackSelector = null
    }
}