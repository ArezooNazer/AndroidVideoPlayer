package com.arezoonazer.player.extension

import androidx.core.net.toUri
import com.arezoonazer.player.argument.VideoSubtitle
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes

/**
 * Subtitle MIME-type:
 *
 * MimeTypes.TEXT_VTT: for webVTT
 * APPLICATION_SUBRIP: for srt
 * see <a href="https://exoplayer.dev/supported-formats.html#sample-formats">Supported formats</a>
 *
 * Selection Flags:
 *
 * SELECTION_FLAG_AUTOSELECT: Indicates that the player may choose to play the track in absence of an explicit user preference. So
 *  in our case, No Subtitle will be selected.
 *  SELECTION_FLAG_DEFAULT: Indicates that the track should be selected if user preferences do not state otherwise.
 *  in our case, the first subtitle will be selected.
 */

fun List<VideoSubtitle>?.toSubtitleMediaItem(): List<MediaItem.SubtitleConfiguration> {
    return this?.map { subtitle ->
        MediaItem.SubtitleConfiguration.Builder(subtitle.url.toUri())
            .setMimeType(MimeTypes.TEXT_VTT)
            .setLabel(subtitle.title)
            .setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT)
            .build()
    }.orEmpty()
}