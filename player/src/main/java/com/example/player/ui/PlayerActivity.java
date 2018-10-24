package com.example.player.ui;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.player.R;
import com.example.player.util.VideoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import static android.widget.Toast.LENGTH_LONG;

public class PlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private VideoPlayer player;

    //other stream type 3
    //private String videoUri = "https://hw6.cdn.asset.aparat.com/aparat-video/22800e8c8e34bc7b232f1139e236e35c12202710-144p__53462.mp4";
    //hls stream type 2
    //private String videoUri = " http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";
    //hls with 8 resolutions
//    private String videoUri = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8";

    private String videoUri = "http://www.storiesinflight.com/js_videosub/jellies.mp4";
    private String subtitleUri = "http://www.storiesinflight.com/js_videosub/jellies.srt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_exoplayer_ui);

        getSupportActionBar().hide();

        playerView = findViewById(R.id.demo_player_view);
        player = new VideoPlayer(playerView, getApplicationContext(), videoUri , subtitleUri);
        player.initializePlayer();

        ImageButton mute = findViewById(R.id.btn_mute);
        mute.setOnClickListener(view -> player.setMute());

        ImageButton repeat = findViewById(R.id.btn_repeat);
        repeat.setOnClickListener(view -> {
            player.setRepeatToggleModes();
            Toast.makeText(getApplicationContext(),"repeat",Toast.LENGTH_SHORT).show();
        });

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
}
