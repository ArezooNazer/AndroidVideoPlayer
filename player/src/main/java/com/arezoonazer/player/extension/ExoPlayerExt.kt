package com.arezoonazer.player.extension

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline

private val timeLinePeriod = Timeline.Period()
private const val NOT_DEFINED_TIME = -1L

fun Player.getLiveStreamCurrentPosition(): Long {
    with(currentTimeline) {
        return if (!isEmpty) {
            currentPosition.minus(
                getPeriod(currentPeriodIndex, timeLinePeriod).positionInWindowMs
            )
        } else {
            NOT_DEFINED_TIME
        }
    }
}