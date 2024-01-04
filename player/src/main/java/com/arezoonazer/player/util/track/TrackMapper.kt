package com.arezoonazer.player.util.track

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector

@OptIn(UnstableApi::class)
class TrackMapper(private val trackSelector: DefaultTrackSelector) {

    private var trackNameIndex = 0
    private val tracks = LinkedHashMap<Int, MediaTrack>()

    fun map(
        rendererType: Int,
        trackNames: List<String>? = null,
    ): List<MediaTrack> {
        tracks.clear()
        val trackInfo = trackSelector.currentMappedTrackInfo ?: return emptyList()
        for (rendererIndex in 0 until trackInfo.rendererCount) {
            if (rendererType != trackInfo.getRendererType(rendererIndex)) {
                continue
            }
            makeTracks(
                trackInfo.getTrackGroups(rendererIndex),
                trackNames,
                index = rendererIndex,
                type = rendererType
            )
        }

        return tracks.map { it.value }
            .sortedBy { it.format.height }
    }

    private fun makeTracks(
        trackGroups: TrackGroupArray,
        trackNames: List<String>?,
        index: Int,
        type: Int,
    ) {
        for (trackGroupsIndex in 0 until trackGroups.length) {
            val trackGroup = trackGroups.get(trackGroupsIndex)
            for (trackGroupIndex in 0 until trackGroup.length) {
                val format = trackGroup.getFormat(trackGroupIndex)

                if (isInValidVideoType(type, format) || isInValidSubtitleType(type, format)) {
                    continue
                }

                val track = MediaTrack(
                    DefaultTrackSelector.SelectionOverride(trackGroupsIndex, trackGroupIndex),
                    trackGroups,
                    format,
                    index,
                    type,
                    getTrackName(trackNames)
                )

                val key = track.trackName.hashCode()
                if (isTrackValidToBeAdded(track, key)) {
                    tracks[key] = track
                } else {
                    continue
                }
            }
        }
    }

    private fun isInValidVideoType(type: Int, format: Format): Boolean {
        return (type == C.TRACK_TYPE_VIDEO && format.height == Format.NO_VALUE)
    }

    private fun isInValidSubtitleType(type: Int, format: Format): Boolean {
        return (type == C.TRACK_TYPE_TEXT && format.sampleMimeType != MimeTypes.TEXT_VTT)
    }

    private fun isTrackValidToBeAdded(track: MediaTrack, key: Int): Boolean {
        tracks[key]?.let { oldTrack ->
            if (oldTrack.format.height > track.format.height) {
                return false
            }
        }
        return true
    }

    private fun getTrackName(trackNames: List<String>?): String? {
        return if (trackNameIndex < trackNames?.size ?: 0) {
            trackNames?.get(trackNameIndex++)
        } else {
            null
        }
    }
}
