package com.example.player.ui;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.player.R;
import com.example.player.util.VideoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private VideoPlayer player;

    //other stream type 3
    //private String videoUri = "https://hw6.cdn.asset.aparat.com/aparat-video/22800e8c8e34bc7b232f1139e236e35c12202710-144p__53462.mp4";
    //hls stream type 2
    //private String videoUri = " http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";
    //hls with 8 resolutions
     private String videoUri = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8";

//    private String videoUri = "http://www.storiesinflight.com/js_videosub/jellies.mp4";
//    private String subtitleUri = "http://www.storiesinflight.com/js_videosub/jellies.srt";

    private String SELECTED_SUBTITLE = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_exoplayer_ui);
        getSupportActionBar().hide();
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        playerView = findViewById(R.id.demo_player_view);
        player = new VideoPlayer(playerView, getApplicationContext(), videoUri, null);
        player.setProgressbar(progressBar);
        player.initializePlayer();

        ImageButton mute = findViewById(R.id.btn_mute);
        mute.setOnClickListener(view -> player.setMute());

        ImageButton repeat = findViewById(R.id.btn_repeat);
        repeat.setOnClickListener(view -> {
            player.setRepeatToggleModes();
            Toast.makeText(getApplicationContext(), "repeat", Toast.LENGTH_SHORT).show();
        });

        ImageButton subtitle = findViewById(R.id.btn_sub);
        subtitle.setOnClickListener(view -> {
//            showSubDialog();
        });

        ImageButton setting = findViewById(R.id.btn_settings);
        setting.setOnClickListener(view -> {
            player.setQuality(this, "select quality");
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

//    public void showSubDialog() {
//        //Some Stuff About AlertDialog
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        final LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.movie_sub_dialog, null);
//        dialogBuilder.setView(dialogView);
//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//
//        //Some Listeners
//        alertDialog.setOnDismissListener(dialog -> alertDialog.dismiss());
//        Button cancel = dialogView.findViewById(R.id.movie_sub_dialog_cancel);
//        cancel.setOnClickListener(v -> alertDialog.dismiss());
//
////        final Switch mSwitch = dialogView.findViewById(R.id.movie_sub_dialog_switch);
////        if (getSubActivated()) {
////            mSwitch.setChecked(true);
////        } else {
////            mSwitch.setChecked(false);
////        }
//
//        TextView sub_en = findViewById(R.id.subtitle1_text_view);
//        sub_en.setOnClickListener(view -> {
//            SELECTED_SUBTITLE = "en";
//        });
//
//        TextView sub_fa = findViewById(R.id.subtitle2_text_view);
//        sub_en.setOnClickListener(view -> {
//            SELECTED_SUBTITLE = "fa";
//        });
//
////        Button done = dialogView.findViewById(R.id.movie_sub_dialog_done);
////        done.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if (mSwitch.isChecked()) {
////                    //Show Subtitles View
////                    playerView.getSubtitleView().setVisibility(View.VISIBLE);
////                    setSubActivated(true);
////
////                } else {
////                    //Hide Subtitles View
////                    playerView.getSubtitleView().setVisibility(View.GONE);
////                    setSubActivated(false);
////                }
////                alertDialog.dismiss();
////            }
////        });
////
////        TextView currentLang = dialogView.findViewById(R.id.movie_sub_dialog_currentLang);
////        currentLang.setText(getSubLanguage());
////
////        //Subtitles Switch Button
////        RelativeLayout language = dialogView.findViewById(R.id.movie_sub_dialog_language);
////        language.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                //Subtitles Language Switching
////                showLanguageDialog();
////                alertDialog.dismiss();
////            }
////        });
//    }


}
