package com.arezoonazer.androidvideoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arezoonazer.player.argument.PlayerParams
import com.arezoonazer.player.argument.VideoSubtitle
import com.arezoonazer.player.extension.startPlayer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startPlayer(getPlayerParam())
    }

    private fun getPlayerParam(): PlayerParams {
        val subtitleList = mutableListOf<VideoSubtitle>().apply {
            add(
                VideoSubtitle(
                    title = "English",
                    url = "https://amara.org/en/subtitles/sbpf8fMnSckL/en/7/download/Big%20Buck%20Bunny.en.vtt"
                )
            )
        }

        return PlayerParams(
            url = "https://5b44cf20b0388.streamlock.net:8443/vod/smil:bbb.smil/playlist.m3u8",
            subtitles = subtitleList
        )
    }
}