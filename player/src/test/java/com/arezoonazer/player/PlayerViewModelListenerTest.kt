package com.arezoonazer.player

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.media3.common.Player
import androidx.media3.common.Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY
import androidx.media3.common.Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS
import androidx.media3.common.Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE
import androidx.media3.common.Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST
import androidx.media3.exoplayer.ExoPlaybackException
import com.arezoonazer.player.repository.PlayerRepository
import com.arezoonazer.player.util.CustomPlaybackState
import com.arezoonazer.player.viewmodel.PlayerViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PlayerViewModelListenerTest {

    @get:Rule
    val instanceTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var savedStateHandle: SavedStateHandle

    @Mock
    private lateinit var playerRepository: PlayerRepository

    private val viewModel: PlayerViewModel by lazy {
        PlayerViewModel(
            savedStateHandle,
            playerRepository
        )
    }

    @Test
    fun `onPlayWhenReadyChanged called with PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST & playWhenReady is true returns PLAYING`() {

        viewModel.getPlayerEventLister().onPlayWhenReadyChanged(
            true,
            PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST
        )

        verifyState(CustomPlaybackState.PLAYING)
    }

    @Test
    fun `onPlayWhenReadyChanged called with PLAY_WHEN_READY_CHANGE_REASON_REMOTE & playWhenReady is true returns PLAYING`() {

        viewModel.getPlayerEventLister().onPlayWhenReadyChanged(
            true,
            PLAY_WHEN_READY_CHANGE_REASON_REMOTE
        )

        verifyState(CustomPlaybackState.PLAYING)
    }

    @Test
    fun `onPlayWhenReadyChanged called with PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST & playWhenReady is false returns PAUSED`() {

        viewModel.getPlayerEventLister().onPlayWhenReadyChanged(
            false,
            PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST
        )

        verifyState(CustomPlaybackState.PAUSED)
    }

    @Test
    fun `onPlayWhenReadyChanged called with PLAY_WHEN_READY_CHANGE_REASON_REMOTE & playWhenReady is false returns PAUSED`() {

        viewModel.getPlayerEventLister().onPlayWhenReadyChanged(
            false,
            PLAY_WHEN_READY_CHANGE_REASON_REMOTE
        )

        verifyState(CustomPlaybackState.PAUSED)
    }

    @Test
    fun `onPlayWhenReadyChanged called with PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS returns PAUSED`() {

        viewModel.getPlayerEventLister().onPlayWhenReadyChanged(
            false,
            PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS
        )

        verifyState(CustomPlaybackState.PAUSED)
    }

    @Test
    fun `onPlayWhenReadyChanged called with PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY returns PAUSED`() {

        viewModel.getPlayerEventLister().onPlayWhenReadyChanged(
            false,
            PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY
        )

        verifyState(CustomPlaybackState.PAUSED)
    }

    @Test
    fun `onPlaybackStateChanged called with STATE_ENDED returns ENDED`() {

        viewModel.getPlayerEventLister().onPlaybackStateChanged(
            Player.STATE_ENDED
        )

        verifyState(CustomPlaybackState.ENDED)
    }

    @Test
    fun `onPlaybackStateChanged called with STATE_READY & player is ready returns PLAYING`() {

        `when`(playerRepository.isPlayerReady()).thenReturn(true)

        viewModel.getPlayerEventLister().onPlaybackStateChanged(
            Player.STATE_READY
        )

        verifyState(CustomPlaybackState.PLAYING)
    }

    @Test
    fun `onPlaybackStateChanged called with STATE_READY & player is not ready returns PAUSED`() {

        `when`(playerRepository.isPlayerReady()).thenReturn(false)

        viewModel.getPlayerEventLister().onPlaybackStateChanged(
            Player.STATE_READY
        )

        verifyState(CustomPlaybackState.PAUSED)
    }

    @Test
    fun `onPlaybackStateChanged called with STATE_BUFFERING returns LOADING`() {

        viewModel.getPlayerEventLister().onPlaybackStateChanged(
            Player.STATE_BUFFERING
        )

        verifyState(CustomPlaybackState.LOADING)
    }

    @Test
    fun `onPlayerError called returns ERROR`() {

        viewModel.getPlayerEventLister().onPlayerError(
            ExoPlaybackException.createForRemote("")
        )

        verifyState(CustomPlaybackState.ERROR)
    }

    private fun verifyState(state: CustomPlaybackState) {
        val value = viewModel.playbackStateLiveData.getOrAwaitValue()
        assert(value == state)
    }
}
