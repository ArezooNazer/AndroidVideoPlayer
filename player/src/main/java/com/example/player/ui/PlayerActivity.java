package com.example.player.ui;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.player.R;
import com.example.player.util.VideoPlayer;
import com.google.android.exoplayer2.audio.AudioFocusManager;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private PlayerView playerView;
    private VideoPlayer player;
    private ImageButton mute, unMute, repeatOff, repeatOne, repeatAll, subtitle, setting;
    private ProgressBar progressBar;

    //other stream type 3
    private String videoUri = "https://hw6.cdn.asset.aparat.com/aparat-video/22800e8c8e34bc7b232f1139e236e35c12202710-144p__53462.mp4";
    //hls stream type 2
    //private String videoUri = " http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";
    //hls with 8 resolutions
//    private String videoUri = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8";

//    private String videoUri = "http://www.storiesinflight.com/js_videosub/jellies.mp4";
//    private String subtitleUri = "http://www.storiesinflight.com/js_videosub/jellies.srt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_exoplayer_ui);
        getSupportActionBar().hide();

        playerView = findViewById(R.id.demo_player_view);
        progressBar = findViewById(R.id.progress_bar);
        mute = findViewById(R.id.btn_mute);
        unMute = findViewById(R.id.btn_unMute);
        repeatOff = findViewById(R.id.btn_repeat_off);
        repeatOne = findViewById(R.id.btn_repeat_one);
        repeatAll = findViewById(R.id.btn_repeat_all);
        subtitle = findViewById(R.id.btn_sub);
        setting = findViewById(R.id.btn_settings);


        player = new VideoPlayer(playerView, getApplicationContext(), videoUri, null);
        player.setProgressbar(progressBar);
//        player.seekToSelectedPosition(300000);
        player.initializePlayer();

        mute.setOnClickListener(this);
        unMute.setOnClickListener(this);
        subtitle.setOnClickListener(this);
        setting.setOnClickListener(this);
        repeatOff.setOnClickListener(this);
        repeatOne.setOnClickListener(this);
        repeatAll.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        player.resumePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        player.resumePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        player.pausePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.releasePlayer();
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onClick(View view) {

        int controllerId = view.getId();
        Log.d("id", "onClick() called with: view = [" + view + "]" + controllerId);

        if (controllerId == R.id.btn_mute) {
            player.setMute(true);
            mute.setVisibility(View.GONE);
            unMute.setVisibility(View.VISIBLE);
        }

        if (controllerId == R.id.btn_unMute) {
            player.setMute(false);
            unMute.setVisibility(View.GONE);
            mute.setVisibility(View.VISIBLE);
        }

        if (controllerId == R.id.btn_repeat_off) {
            player.setRepeatToggleModes(1);
            repeatOff.setVisibility(View.GONE);
            repeatOne.setVisibility(View.VISIBLE);
        }

        if (controllerId == R.id.btn_repeat_one) {
            player.setRepeatToggleModes(2);
            repeatOne.setVisibility(View.GONE);
            repeatAll.setVisibility(View.VISIBLE);
        }

        if (controllerId == R.id.btn_repeat_all) {
            player.setRepeatToggleModes(0);
            repeatAll.setVisibility(View.GONE);
            repeatOff.setVisibility(View.VISIBLE);
        }

        if (controllerId == R.id.btn_settings) {
            player.setQuality(this, "select quality");
        }
    }
}
