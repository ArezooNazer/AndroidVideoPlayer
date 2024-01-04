package com.arezoonazer.player.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.arezoonazer.player.argument.PlayerParams
import com.arezoonazer.player.repository.PlayerRepository
import com.arezoonazer.player.util.CustomPlaybackState
import com.arezoonazer.player.util.createMediaItem
import com.arezoonazer.player.view.PlayerActivity.Companion.PLAYER_PARAMS_EXTRA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playerRepository: PlayerRepository,
) : ViewModel() {

    private val _playerLiveData = MutableLiveData<ExoPlayer>()
    val playerLiveData: LiveData<ExoPlayer> = _playerLiveData

    private val _playbackStateLiveData = MutableLiveData(CustomPlaybackState.LOADING)
    val playbackStateLiveData: LiveData<CustomPlaybackState> = _playbackStateLiveData

    private val _isMuteLiveData = MutableLiveData(false)
    val isMuteLiveData: LiveData<Boolean> = _isMuteLiveData

    private var playerEventListener: Player.Listener? = getPlayerEventLister()

    private val playerParams = savedStateHandle.get<PlayerParams>(PLAYER_PARAMS_EXTRA)

    override fun onCleared() {
        super.onCleared()
        release()
    }

    fun onActivityCreate(context: Context) {
        viewModelScope.launch {
            if (_playerLiveData.value == null) {
                setupPlayer(context)
            }
        }
    }

    fun onPlayButtonClicked() {
        if (playbackStateLiveData.value != CustomPlaybackState.ERROR) {
            playerRepository.togglePlayingState()
        }
    }

    fun onReplayClicked() {
        when (playbackStateLiveData.value) {
            CustomPlaybackState.ERROR -> {
                playerRepository.rePrepareAndPlay()
            }

            CustomPlaybackState.ENDED -> {
                playerRepository.seekToDefaultPosition()
            }

            else -> return
        }
    }

    fun onMuteClicked() {
        with(playerRepository) {
            toggleMuteState()
            _isMuteLiveData.value = isMute()
        }
    }

    private fun setupPlayer(context: Context) {
        if (playerParams == null) {
            Log.e(TAG, "setupPlayer: playerParams is null")
            return
        }
        with(playerRepository) {
            _playerLiveData.value = createPlayer(context)
            preparePlayer(createMediaItem(playerParams))
            addEventListener(playerEventListener)
            play()
        }
    }

    private fun release() {
        with(playerRepository) {
            removeEventListener(playerEventListener)
            playerEventListener = null
            release()
        }
    }

    internal fun getPlayerEventLister(): Player.Listener = object : Player.Listener {

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            _playbackStateLiveData.value = when (reason) {
                Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST,
                Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE,
                -> {
                    if (playWhenReady) {
                        CustomPlaybackState.PLAYING
                    } else {
                        CustomPlaybackState.PAUSED
                    }
                }

                Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS,
                Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY,
                -> CustomPlaybackState.PAUSED

                else -> return
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            _playbackStateLiveData.value = when (state) {
                Player.STATE_ENDED -> CustomPlaybackState.ENDED
                Player.STATE_READY -> {
                    if (playerRepository.isPlayerReady()) {
                        CustomPlaybackState.PLAYING
                    } else {
                        CustomPlaybackState.PAUSED
                    }
                }

                Player.STATE_BUFFERING -> CustomPlaybackState.LOADING
                else -> return
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            if (events.contains(Player.EVENT_IS_LOADING_CHANGED)) {
                val isLoading = player.isLoading
                val isNotPlaying = player.isPlaying.not()
                if (isLoading && isNotPlaying) {
                    _playbackStateLiveData.value = CustomPlaybackState.LOADING
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            _playbackStateLiveData.value = CustomPlaybackState.ERROR
        }
    }
}

private const val TAG = "PlayerViewModel"
