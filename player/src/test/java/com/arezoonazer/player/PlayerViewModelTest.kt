package com.arezoonazer.player

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.ExoPlayer
import com.arezoonazer.player.argument.PlayerParams
import com.arezoonazer.player.repository.PlayerRepository
import com.arezoonazer.player.viewmodel.PlayerViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class PlayerViewModelTest {

    @get:Rule
    val instanceTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var savedStateHandle: SavedStateHandle

    @Mock
    private lateinit var playerParams: PlayerParams

    @Mock
    private lateinit var playerRepository: PlayerRepository

    private val viewModel: PlayerViewModel by lazy {
        PlayerViewModel(
            savedStateHandle,
            playerRepository
        )
    }

    @Test
    fun `onActivityCreated called for the first time then setupPlayer`() {
        val observer = mock<Observer<ExoPlayer>>()
        val mockedExoPlayer = mock<ExoPlayer>()

        whenever(savedStateHandle.get<PlayerParams>(any())).doReturn(playerParams)
        whenever(playerRepository.createPlayer(context)).doReturn(mockedExoPlayer)
        viewModel.playerLiveData.observeForever(observer)

        viewModel.onActivityCreate(context)

        verify(observer, times(1)).onChanged(mockedExoPlayer)
    }

    @Test
    fun `onActivityCreated called more than once then return`() {
        val observer = mock<Observer<ExoPlayer>>()
        val mockedExoPlayer = mock<ExoPlayer>()

        whenever(savedStateHandle.get<PlayerParams>(any())).doReturn(playerParams)
        whenever(playerRepository.createPlayer(context)).doReturn(mockedExoPlayer)
        viewModel.onActivityCreate(context)
        viewModel.playerLiveData.observeForever(observer)

        viewModel.onActivityCreate(context)

        verify(observer, times(1)).onChanged(mockedExoPlayer)
    }

    @Test
    fun `onPlayButtonClicked called without playback error then togglePlayingState `() {
        viewModel.onPlayButtonClicked()

        verify(playerRepository, times(1)).togglePlayingState()
    }

    @Test
    fun `onPlayButtonClicked called when playback got error then return `() {
        viewModel.getPlayerEventLister().onPlayerError(
            ExoPlaybackException.createForRemote("")
        )

        viewModel.onPlayButtonClicked()

        verify(playerRepository, never()).togglePlayingState()
    }

    @Test
    fun `onReplayClicked called when playback state is ERROR then retry `() {
        viewModel.getPlayerEventLister().onPlayerError(
            ExoPlaybackException.createForRemote("")
        )

        viewModel.onReplayClicked()

        verify(playerRepository, times(1)).rePrepareAndPlay()
    }

    @Test
    fun `onReplayClicked called when playback state is ENED then replay `() {
        viewModel.getPlayerEventLister().onPlaybackStateChanged(
            Player.STATE_ENDED
        )

        viewModel.onReplayClicked()

        verify(playerRepository, times(1)).seekToDefaultPosition()
    }

    @Test
    fun `onMuteClicked called then update icon`() {
        val observer = mock<Observer<Boolean>>()

        viewModel.isMuteLiveData.observeForever(observer)
        clearInvocations(observer)

        viewModel.onMuteClicked()

        verify(observer, times(1)).onChanged(any())
    }
}
