package com.arezoonazer.player.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.arezoonazer.player.R

class MuteButton constructor(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    init {
        setMuteState(isMute = false)
    }

    fun setMuteState(isMute: Boolean) {
        val icon = if (isMute) {
            R.drawable.ic_exo_mute
        } else {
            R.drawable.ic_exo_unmute
        }
        setImageResource(icon)
    }
}