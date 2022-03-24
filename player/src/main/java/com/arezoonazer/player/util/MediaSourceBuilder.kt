package com.arezoonazer.player.util

import android.net.Uri
import androidx.core.net.toUri
import com.arezoonazer.player.argument.PlayerParams
import com.arezoonazer.player.extension.toSubtitleMediaItem
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util

fun createMediaItem(playerParams: PlayerParams): MediaItem {
    val uri = playerParams.url.toUri()
    return MediaItem.Builder()
        .setUri(uri)
        .setMimeType(getUriMimeType(uri))
        .setSubtitleConfigurations(playerParams.subtitles.toSubtitleMediaItem())
        .build()
}

private fun getUriMimeType(uri: Uri): String {
    return when (val type = Util.inferContentType(uri)) {
        C.TYPE_HLS -> {
            MimeTypes.APPLICATION_M3U8
        }
        C.TYPE_DASH -> {
            MimeTypes.APPLICATION_MPD
        }
        C.TYPE_SS -> {
            MimeTypes.APPLICATION_SS
        }
        C.TYPE_OTHER -> {
            MimeTypes.APPLICATION_MP4
        }
        else -> {
            throw IllegalStateException("Unsupported type: $type")
        }
    }
}