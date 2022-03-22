package com.arezoonazer.player.di

import com.arezoonazer.player.argument.PlayerParams
import com.arezoonazer.player.viewmodel.PlayerViewModel

@dagger.assisted.AssistedFactory
interface AssistedFactory {
    fun create(playerParams: PlayerParams): PlayerViewModel
}