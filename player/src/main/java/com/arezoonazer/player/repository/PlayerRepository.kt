package com.arezoonazer.player.repository

import android.content.Context
import com.arezoonazer.player.datasource.TrackSelectorDataSource
import com.arezoonazer.player.extension.getLiveStreamCurrentPosition
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class PlayerRepository @Inject constructor(
    private val trackSelectorDataSource: TrackSelectorDataSource
) {

    private var _player: ExoPlayer? = null
    val player: ExoPlayer
        get() = requireNotNull(_player)

    fun createPlayer(context: Context): ExoPlayer {
        _player = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelectorDataSource.trackSelector)
            .setSeekForwardIncrementMs(SEEK_INTERVAL)
            .setSeekBackIncrementMs(SEEK_INTERVAL)
            .build()
        return player
    }

    fun preparePlayer(mediaItem: MediaItem) {
        player.apply {
            setMediaItem(mediaItem)
            prepare()
        }
    }

    fun rePrepareAndPlay() {
        player.apply {
            prepare()
            play()
        }
    }

    fun seekToDefaultPosition() {
        player.seekToDefaultPosition()
    }

    fun play() {
        player.playWhenReady = true
    }

    fun pause() {
        player.playWhenReady = false
    }

    fun togglePlayingState() {
        player.playWhenReady = isPlayerReady().not()
    }

    fun rewind(seekDuration: Int) {
        with(player) {
            seekTo(
                currentMediaItemIndex,
                max(0, currentPosition - seekDuration)
            )
        }
    }

    fun forward(seekDuration: Int) {
        with(player) {
            seekTo(
                currentMediaItemIndex,
                min(duration, currentPosition + seekDuration)
            )
        }
    }

    fun toggleMuteState() {
        player.volume = if (isMute()) {
            1f
        } else {
            0f
        }
    }

    fun isMute(): Boolean {
        return player.volume == 0F
    }

    fun isPlayingStreamLive(): Boolean {
        return player.isCurrentMediaItemLive
    }

    fun isPlayerReady(): Boolean {
        return player.playWhenReady
    }

    fun addEventListener(playerEventListener: Player.Listener?) {
        playerEventListener?.let { player.addListener(it) }
    }

    fun removeEventListener(playerEventListener: Player.Listener?) {
        playerEventListener?.let { player.removeListener(it) }
    }

    fun getCurrentPosition(): Long {
        player.run {
            return if (isPlayingStreamLive()) {
                getLiveStreamCurrentPosition()
            } else {
                currentPosition
            }
        }
    }

    fun getPlaybackDuration(): Long = player.duration

    fun release() {
        trackSelectorDataSource.nullifyTrackSelector()
        player.clearMediaItems()
        player.release()
    }

    companion object{
        private const val SEEK_INTERVAL = 10000L
    }
}