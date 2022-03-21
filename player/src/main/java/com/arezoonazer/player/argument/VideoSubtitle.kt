package com.arezoonazer.player.argument

import java.io.Serializable

data class VideoSubtitle(
    val title: String,
    val url: String
) : Serializable