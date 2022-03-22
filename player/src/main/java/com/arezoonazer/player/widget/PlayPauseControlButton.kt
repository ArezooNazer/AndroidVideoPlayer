package com.arezoonazer.player.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.arezoonazer.player.R
import com.arezoonazer.player.extension.dpToPx
import com.arezoonazer.player.util.CustomPlaybackState

class PlayPauseControlButton : FrameLayout {

    private var button = AppCompatImageView(context)

    constructor(context: Context) : super(context) {
        initComponents()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initComponents()
    }

    private fun initComponents() {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        initButton()
    }

    private fun initButton() {
        val buttonSize = context.dpToPx(resources.getDimension(R.dimen.player_play_icon_size))
        val params = LayoutParams(buttonSize, buttonSize)
        button.apply {
            if (isInEditMode) {
                button.setImageResource(R.drawable.ic_exo_play)
            } else {
                visibility = View.GONE
            }
        }

        addView(button, params)
    }

    fun setState(playbackState: CustomPlaybackState) {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (playbackState) {
            CustomPlaybackState.PLAYING -> {
                button.setImageResource(R.drawable.ic_exo_pause)
                button.visibility = View.VISIBLE
            }
            CustomPlaybackState.PAUSED -> {
                button.setImageResource(R.drawable.ic_exo_play)
                button.visibility = View.VISIBLE
            }
            CustomPlaybackState.LOADING -> {
                button.visibility = View.INVISIBLE
            }
            CustomPlaybackState.ERROR, CustomPlaybackState.ENDED -> {
                button.visibility = View.INVISIBLE
            }
        }
    }

    override fun setOnClickListener(onClickListener: OnClickListener?) {
        button.setOnClickListener(onClickListener)
    }
}