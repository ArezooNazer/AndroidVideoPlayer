package com.arezoonazer.player.extension

import androidx.core.net.toUri
import com.arezoonazer.player.argument.VideoSubtitle
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes

fun List<VideoSubtitle>?.toSubtitleMediaItem(): List<MediaItem.SubtitleConfiguration> {
    return this?.map { subtitle ->
        MediaItem.SubtitleConfiguration.Builder(subtitle.url.toUri())
            .setMimeType(MimeTypes.TEXT_VTT)
            .setLabel(subtitle.title)
            .setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT)
            .build()
    }.orEmpty()
}