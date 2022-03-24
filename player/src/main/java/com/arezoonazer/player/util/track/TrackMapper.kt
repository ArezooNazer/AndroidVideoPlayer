package com.arezoonazer.player.util.track

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes

class TrackMapper(private val trackSelector: DefaultTrackSelector) {

    private var trackNameIndex = 0
    private val tracks = LinkedHashMap<Int, MediaTrack>()

    fun map(
        rendererType: Int,
        trackNames: List<String>? = null
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
        type: Int
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