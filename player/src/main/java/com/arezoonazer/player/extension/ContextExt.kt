package com.arezoonazer.player.extension

import android.content.Context
import kotlin.math.roundToInt

fun Context.dpToPx(dp: Float): Int = (dp * this.resources.displayMetrics.density).roundToInt()