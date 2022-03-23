package com.arezoonazer.player.extension

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.ImageButton
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun ImageButton.setImageButtonTintColor(@ColorRes colorRes: Int) {
    val tintColor = ContextCompat.getColor(this.context, colorRes)
    colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
}