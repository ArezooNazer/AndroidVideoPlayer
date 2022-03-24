package com.arezoonazer.player.di

import com.arezoonazer.player.argument.VideoSubtitle
import com.arezoonazer.player.viewmodel.SubtitleViewModel

@dagger.assisted.AssistedFactory
interface SubtitleViewModelAssistedFactory {
    fun create(videoSubtitles: List<VideoSubtitle>): SubtitleViewModel
}