package com.example.player.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.player.R;
import com.example.player.util.Url;
import com.example.player.util.VideoPlayer;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private PlayerView playerView;
    private VideoPlayer player;
    private ImageButton mute, unMute, repeatOff, repeatOne, repeatAll, subtitle, setting, lock, unLock;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private int REPEAT_OFF = 0;
    private int REPEAT_ONE = 1;
    private int REPEAT_ALL = 2;

    /***********************************************************
     sample video and subtitles
     ***********************************************************/

    private String videoUri = Url.getVideoUri();
    private String subtitleUri = Url.getSubtitleUri();

    private List<String> videoUriList = new ArrayList<>();

    private void makeListOfUri(){

        videoUriList.add("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8");
        videoUriList.add("http://www.storiesinflight.com/js_videosub/jellies.mp4");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();


        playerView = findViewById(R.id.demo_player_view);
        progressBar = findViewById(R.id.progress_bar);

        mute = findViewById(R.id.btn_mute);
        unMute = findViewById(R.id.btn_unMute);
//        repeatOff = findViewById(R.id.btn_repeat_off);
//        repeatOne = findViewById(R.id.btn_repeat_one);
//        repeatAll = findViewById(R.id.btn_repeat_all);
        subtitle = findViewById(R.id.btn_subtitle);
        setting = findViewById(R.id.btn_settings);
        lock = findViewById(R.id.btn_lock);
        unLock = findViewById(R.id.btn_unLock);


//        player = new VideoPlayer(playerView, getApplicationContext(), videoUri);

        makeListOfUri();
        player = new VideoPlayer(playerView, getApplicationContext(), videoUriList);

        //optional setting
        playerView.getSubtitleView().setVisibility(View.GONE);
        player.setProgressbar(progressBar);
        player.seekToOnDoubleTap();

        //start video from selected time
        player.seekToSelectedPosition(0, 0, 0);

        Log.d("duration", "video duration >> " + player.getPlayer().getDuration());

        mute.setOnClickListener(this);
        unMute.setOnClickListener(this);
        subtitle.setOnClickListener(this);
        setting.setOnClickListener(this);
        lock.setOnClickListener(this);
        unLock.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        hideSystemUi();
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUi();

    }

    @Override
    public void onClick(View view) {

        int controllerId = view.getId();
        Log.d("id", "onClick() called with: view = [" + view + "]" + controllerId);

        if (controllerId == R.id.btn_mute) {
            updateMuteMode(true);
        }

        if (controllerId == R.id.btn_unMute) {
            updateMuteMode(false);
        }

        if (controllerId == R.id.btn_settings) {
            player.setSelectedQuality(this, "select quality");
        }

        if (controllerId == R.id.btn_subtitle) {
            if (playerView.getSubtitleView().getVisibility() == View.VISIBLE)
                showSubtitle(false);
            else
                showSubtitle(true);
        }

        if (controllerId == R.id.btn_lock) {
            updateLockMode(true);
        }

        if (controllerId == R.id.btn_unLock) {
            updateLockMode(false);
        }

    }

    /***********************************************************
     UI config
     ***********************************************************/
    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void showSubtitleDialog() {
        if (player != null && playerView.getSubtitleView() != null) {

            player.pausePlayer();
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);

            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.track_selection_dialog, null);
            builder.setView(view);

            alertDialog = builder.create();

            // set the height and width of dialog
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;

            alertDialog.getWindow().setAttributes(layoutParams);

            // to prevent dialog box from getting dismissed on outside touch
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

            TextView persianSub = view.findViewById(R.id.subtitle_fa);
            TextView englishSub = view.findViewById(R.id.subtitle_en);
            Button cancelDialog = view.findViewById(R.id.cancel_dialog_btn);

            //you can use recyclerView for list of subtitles
            persianSub.setOnClickListener(view1 -> {
                player.setSelectedSubtitle(subtitleUri);
                playerView.getSubtitleView().setVisibility(View.VISIBLE);
                alertDialog.dismiss();
            });

            englishSub.setOnClickListener(view1 -> {
                player.setSelectedSubtitle(subtitleUri);
                playerView.getSubtitleView().setVisibility(View.VISIBLE);
                alertDialog.dismiss();
            });

            cancelDialog.setOnClickListener(view1 -> {
                alertDialog.dismiss();
                player.resumePlayer();
            });
        }
    }

    private void showSubtitle(boolean show) {
        if (player != null && playerView.getSubtitleView() != null) {
            if (show) {
                showSubtitleDialog();
            } else
                playerView.getSubtitleView().setVisibility(View.GONE);
        }
    }

    private void updateMuteMode(boolean isMute) {
        if (player != null && playerView != null) {
            if (isMute) {
                player.setMute(true);
                mute.setVisibility(View.GONE);
                unMute.setVisibility(View.VISIBLE);
            } else {
                player.setMute(false);
                unMute.setVisibility(View.GONE);
                mute.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateLockMode(boolean isLock) {
        if (player != null && playerView != null) {
            player.setPlayerViewListener(isLock);
            if (isLock) {
                playerView.hideController();
                unLock.setVisibility(View.VISIBLE);

            } else {
                playerView.showController();
                unLock.setVisibility(View.GONE);
            }
        }
    }

}
