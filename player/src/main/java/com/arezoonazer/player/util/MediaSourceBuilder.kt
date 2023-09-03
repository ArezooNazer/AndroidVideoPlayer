package com.arezoonazer.player.util

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.Util
import com.arezoonazer.player.argument.PlayerParams
import com.arezoonazer.player.extension.toSubtitleMediaItem

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
        C.CONTENT_TYPE_HLS -> {
            MimeTypes.APPLICATION_M3U8
        }

        C.CONTENT_TYPE_DASH -> {
            MimeTypes.APPLICATION_MPD
        }

        C.CONTENT_TYPE_SS -> {
            MimeTypes.APPLICATION_SS
        }

        C.CONTENT_TYPE_OTHER -> {
            MimeTypes.APPLICATION_MP4
        }

        else -> {
            throw IllegalStateException("Unsupported type: $type")
        }
    }
}
