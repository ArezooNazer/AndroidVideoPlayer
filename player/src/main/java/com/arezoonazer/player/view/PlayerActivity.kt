package com.arezoonazer.player.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arezoonazer.player.argument.PlayerParams
import com.arezoonazer.player.databinding.ActivityPlayerBinding
import com.arezoonazer.player.extension.hideSystemUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private val binding: ActivityPlayerBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    private val playerParams: PlayerParams by lazy(LazyThreadSafetyMode.NONE) {
        intent.getSerializableExtra(PLAYER_PARAMS_EXTRA) as PlayerParams
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        resolveSystemGestureConflict()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI(binding.root)
    }

    private fun resolveSystemGestureConflict() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { _, insets ->
            val systemGestureInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            WindowInsetsCompat.Builder()
                .setInsets(WindowInsetsCompat.Type.systemBars(), systemGestureInsets)
                .setInsets(WindowInsetsCompat.Type.systemGestures(), systemGestureInsets)
                .build()
        }
    }

    companion object {
        const val PLAYER_PARAMS_EXTRA = "playerParamsExtra"
    }
}