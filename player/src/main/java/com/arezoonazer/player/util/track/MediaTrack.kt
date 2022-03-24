package com.arezoonazer.player.util.track

import android.util.SparseArray
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

class MediaTrack(
    val selectionOverride: DefaultTrackSelector.SelectionOverride,
    val trackGroupArray: TrackGroupArray,
    val format: Format,
    val rendererIndex: Int,
    val rendererType: Int,
    rawTrackName: String? = null
) {

    val trackName: String by lazy {
        rawTrackName ?: when (rendererType) {
            C.TRACK_TYPE_TEXT -> format.language ?: format.label ?: ""
            C.TRACK_TYPE_VIDEO -> findVideoTrackName(format)
            else -> ""
        }
    }

    override fun toString(): String {
        return "trackName: $trackName, height: ${format.height}"
    }

    companion object {

        private const val QUALITY_144 = 144
        private const val QUALITY_240 = 240
        private const val QUALITY_360 = 360
        private const val QUALITY_480 = 480
        private const val QUALITY_720 = 720
        private const val QUALITY_1080 = 1080

        private val qualityInfo: SparseArray<String> by lazy {
            SparseArray<String>().apply {
                put(QUALITY_144, "144p")
                put(QUALITY_240, "240p")
                put(QUALITY_360, "360p")
                put(QUALITY_480, "480p")
                put(QUALITY_720, "720p")
                put(QUALITY_1080, "1080p")
            }
        }

        private fun findVideoTrackName(format: Format): String {
            val height = format.height

            var closestQualityString = qualityInfo.valueAt(0)
            var closestHeightDistance = getDistance(
                height,
                qualityInfo.keyAt(0)
            )

            for (i in 1 until qualityInfo.size()) {
                val distance = getDistance(
                    height,
                    qualityInfo.keyAt(i)
                )
                if (distance < closestHeightDistance) {
                    closestHeightDistance = distance
                    closestQualityString = qualityInfo.valueAt(i)
                }
            }

            return closestQualityString
        }

        private fun getDistance(a: Int, b: Int): Int {
            return Math.abs(a - b)
        }
    }
}