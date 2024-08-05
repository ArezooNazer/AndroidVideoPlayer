package com.arezoonazer.androidvideoplayer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.arezoonazer.androidvideoplayer.databinding.ActivityMainBinding
import com.arezoonazer.player.argument.PlayerParams
import com.arezoonazer.player.argument.VideoSubtitle
import com.arezoonazer.player.extension.startPlayer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var skb_brightness: SeekBar

    private val binding: ActivityMainBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        skb_brightness = findViewById(R.id.seekBar)

        Settings.System.putInt(
            this.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )

        with(binding) {
            playButton.setOnClickListener { startPlayer(getPlayerParam()) }

            videoUrlEditText.doOnTextChanged { text, _, _, _ ->
                customPlayButton.isEnabled = text.isNullOrEmpty().not()
            }

            customPlayButton.setOnClickListener { onCustomPlayButtonClicked() }
        }

        skb_brightness.max = 255
        skb_brightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (canWriteSettings) {

                    val lp = window.attributes
                    lp.screenBrightness = progress.toFloat() / 255
                    window.attributes = lp
                } else {
                    startManageWriteSettingsPermission()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
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

    private fun onCustomPlayButtonClicked() {
        with(binding) {
            val videoUrl = videoUrlEditText.text.toString()

            if (videoUrl.isEmpty()) {
                showToast("Please insert video url")
                return
            }

            val subtitleUrl1 = subtitle1UrlEditText.text.toString()
            val subtitleUrl2 = subtitle2UrlEditText.text.toString()

            val subtitleList = mutableListOf<VideoSubtitle>().apply {
                if (subtitleUrl1.isEmpty().not()) {
                    add(
                        VideoSubtitle(
                            title = "subtitle 1",
                            url = subtitleUrl1
                        )
                    )
                }

                if (subtitleUrl2.isEmpty().not()) {
                    add(
                        VideoSubtitle(
                            title = "subtitle 2",
                            url = subtitleUrl2
                        )
                    )
                }
            }

            startPlayer(PlayerParams(videoUrl, subtitleList))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, LENGTH_SHORT).show()
    }

    private fun startManageWriteSettingsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(
                Settings.ACTION_MANAGE_WRITE_SETTINGS,
                Uri.parse("package:${this.packageName}")
            ).let {
                startActivityForResult(it, 5)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1 -> {
                if (this.canWriteSettings) {
                    // change the settings here ...
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Write settings permission is not granted!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    val canWriteSettings: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(this)


}