package com.arezoonazer.player.argument

import java.io.Serializable

data class PlayerParams(
    val url: String,
    val subtitles: List<VideoSubtitle> = emptyList()
) : Serializable