package com.arezoonazer.player.extension

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.arezoonazer.player.argument.PlayerParams
import com.arezoonazer.player.view.PlayerActivity

fun Activity.startPlayer(playerParams: PlayerParams) {
    Intent(this, PlayerActivity::class.java).apply {
        putExtra(PlayerActivity.PLAYER_PARAMS_EXTRA, playerParams)
    }.also { playerIntent ->
        startActivity(playerIntent)
    }
}

fun Activity.hideSystemUI(rootView: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        setEdgeToEdgeMode(rootView)
    } else {
        setFullScreen()
    }
}

fun Activity.resolveSystemGestureConflict() {
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { _, insets ->
        val systemGestureInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        WindowInsetsCompat.Builder()
            .setInsets(WindowInsetsCompat.Type.systemBars(), systemGestureInsets)
            .setInsets(WindowInsetsCompat.Type.systemGestures(), systemGestureInsets)
            .build()
    }
}

private fun Activity.setEdgeToEdgeMode(rootView: View) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowCompat.getInsetsController(window, rootView)?.let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@Suppress("DEPRECATION")
private fun Activity.setFullScreen() {
    window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
}