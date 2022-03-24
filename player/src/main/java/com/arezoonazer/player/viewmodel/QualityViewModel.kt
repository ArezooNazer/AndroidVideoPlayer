package com.arezoonazer.player.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arezoonazer.player.R
import com.arezoonazer.player.datasource.TrackSelectorDataSource
import com.arezoonazer.player.repository.PlayerRepository
import com.arezoonazer.player.util.track.MediaTrack
import com.arezoonazer.player.util.track.TrackEntity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QualityViewModel @Inject constructor(
    private val trackSelectorDataSource: TrackSelectorDataSource,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _qualityEntitiesLiveData = MutableLiveData<List<TrackEntity>>()
    val qualityEntitiesLiveData: LiveData<List<TrackEntity>> = _qualityEntitiesLiveData

    private val _onQualitySelectedLiveData = MutableLiveData<Unit>()
    val onQualitySelectedLiveData: LiveData<Unit> = _onQualitySelectedLiveData

    private val autoText = R.string.player_automatic

    private var qualities = mutableListOf<MediaTrack>()
    private var selectedQuality: TrackEntity? = null

    var playerEventListener: Player.Listener? = getPlayerEventLister()
        private set

    override fun onCleared() {
        super.onCleared()
        resetState()
    }

    fun onActivityCreated() {
        if (qualities.isNotEmpty()) {
            return
        }
        playerRepository.addEventListener(playerEventListener)
    }

    private fun onQualitySelected(index: Int) {
        if (isInvalidSelectedQuality(index)) {
            return
        }
        _onQualitySelectedLiveData.value = Unit
        with(trackSelectorDataSource) {
            val selectedQualityCafeTrack = updateSelectedTrack(
                index,
                qualities,
                qualities[0].rendererIndex
            )
            updateTrackEntities(
                qualities,
                selectedQualityCafeTrack,
                autoText,
                ::onQualitySelected
            )?.let { tracks ->
                _qualityEntitiesLiveData.value = tracks
                selectedQuality = qualityEntitiesLiveData.value?.get(index)
            }
        }
    }


    private fun getPlayerEventLister(): Player.Listener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            if (state == STATE_READY && qualities.isEmpty()) {
                setupQualityConfig()
            }
        }
    }

    private fun setupQualityConfig() {
        trackSelectorDataSource.trackMapper.run {
            qualities.apply {
                clear()
                addAll(map(C.TRACK_TYPE_VIDEO))
            }
            initTrackEntities()
        }
    }

    private fun initTrackEntities() {
        trackSelectorDataSource.updateTrackEntities(
            qualities,
            null,
            autoText,
            ::onQualitySelected
        )?.let { tracks ->
            _qualityEntitiesLiveData.value = tracks
            selectedQuality = tracks.firstOrNull { it.isSelected }
        }
    }

    private fun isInvalidSelectedQuality(index: Int): Boolean {
        return qualities.isEmpty() || isValidIndex(index).not()
    }

    private fun isValidIndex(index: Int): Boolean {
        return index >= 0 && index <= qualities.size
    }

    private fun resetState() {
        playerRepository.removeEventListener(playerEventListener)
        playerEventListener = null
        qualities.clear()
        selectedQuality = null
    }
}