package com.example.player.ui;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.player.R;
import com.example.player.db.SubtitleUrl;
import com.example.player.db.UrlDatabase;
import com.example.player.db.VideoUrl;
import com.example.player.util.PlayerUiController;
import com.example.player.util.SubtitleAdapter;
import com.example.player.util.VideoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, PlayerUiController {

    private static final String TAG = "PlayerActivity";
    private PlayerView playerView;
    private VideoPlayer player;
    private ImageButton mute, unMute, subtitle, setting, lock, unLock;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    public static UrlDatabase urlDatabase;


    /***********************************************************
     sample video and subtitles
     ***********************************************************/

    private String videoUri = "http://www.storiesinflight.com/js_videosub/jellies.mp4";
    private String subtitleUri = "http://www.storiesinflight.com/js_videosub/jellies.srt";

    private List<VideoUrl> videoUriList;
    private List<SubtitleUrl> subtitleList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();

        initializeDb();

        playerView = findViewById(R.id.demo_player_view);
        progressBar = findViewById(R.id.progress_bar);

        mute = findViewById(R.id.btn_mute);
        unMute = findViewById(R.id.btn_unMute);
        subtitle = findViewById(R.id.btn_subtitle);
        setting = findViewById(R.id.btn_settings);
        lock = findViewById(R.id.btn_lock);
        unLock = findViewById(R.id.btn_unLock);


//        player = new VideoPlayer(playerView, getApplicationContext(), videoUri);

        player = new VideoPlayer(playerView, getApplicationContext(), urlDatabase.urlDao().getAllUrls(), this);

        //optional setting
        playerView.getSubtitleView().setVisibility(View.GONE);
        player.seekToOnDoubleTap();

        //start video from selected time
        player.seekToSelectedPosition(0, 0, 0);


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

    private void initializeDb() {
        urlDatabase = Room.databaseBuilder(getApplicationContext(), UrlDatabase.class, "URL_DB")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

//        urls added before
        makeListOfUri();
    }

    private void makeListOfUri() {
        videoUriList = new ArrayList<>();
        subtitleList = new ArrayList<>();

        videoUriList.add(new VideoUrl("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"));
        videoUriList.add(new VideoUrl("http://cdn.theoplayer.com/video/big_buck_bunny/big_buck_bunny_metadata.m3u8"));
        videoUriList.add(new VideoUrl("http://www.storiesinflight.com/js_videosub/jellies.mp4"));

        subtitleList.add(new SubtitleUrl(2, "Farsi", "https://download.blender.org/durian/subs/sintel_fa.srt"));
        subtitleList.add(new SubtitleUrl(2, "English", "https://bitdash-a.akamaihd.net/content/sintel/hls/subtitles_en.vtt"));
        subtitleList.add(new SubtitleUrl(2, "French", "https://durian.blender.org/wp-content/content/subtitles/sintel_fr.srt"));

        subtitleList.add(new SubtitleUrl(3, "English", "http://www.storiesinflight.com/js_videosub/jellies.srt"));

        urlDatabase.urlDao().insertAllVideoUrl(videoUriList);
        urlDatabase.urlDao().insertAllSubtitleUrl(subtitleList);
    }


    @Override
    public void onClick(View view) {

        int controllerId = view.getId();
        Log.d("id", "onClick() called with: view = [" + view + "]" + controllerId);

        if (controllerId == R.id.btn_mute) {
            player.setMute(true);
        }

        if (controllerId == R.id.btn_unMute) {
            player.setMute(false);
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

    private void showSubtitle(boolean show) {
        if (player != null && playerView.getSubtitleView() != null) {
            if (show) {
                if (player != null && player.getPlayer().getCurrentTimeline() != null) {
                    int currentVideoId = player.getPlayer().getCurrentWindowIndex() + 1;
                    long currentPosition = player.getPlayer().getCurrentPosition();
                    List<SubtitleUrl> subtitleUrlList = urlDatabase.urlDao().getAllSubtitles(currentVideoId);

                    if (subtitleUrlList.size() != 0)
                        showSubtitleDialog(subtitleUrlList, currentVideoId, currentPosition);
                    else
                        Toast.makeText(this, "برای این ویدیو زیرنویسی وجود ندارد", Toast.LENGTH_SHORT).show();
                }
            } else
                playerView.getSubtitleView().setVisibility(View.GONE);
        }
    }

    private void showSubtitleDialog(List<SubtitleUrl> subtitleList, int videoId, long currentPosition) {
        if (player != null && playerView.getSubtitleView() != null) {

            player.pausePlayer();
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);

            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.subtitle_selection_dialog, null);
            builder.setView(view);

            alertDialog = builder.create();

            // set the height and width of dialog
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;

            alertDialog.getWindow().setAttributes(layoutParams);

            RecyclerView recyclerView = view.findViewById(R.id.subtitle_recycler_view);
            recyclerView.setAdapter(new SubtitleAdapter(subtitleList, player, playerView, alertDialog, videoId, currentPosition));

            Button cancelDialog = view.findViewById(R.id.cancel_dialog_btn);
            cancelDialog.setOnClickListener(view1 -> {
                alertDialog.dismiss();
                player.resumePlayer();
            });

            // to prevent dialog box from getting dismissed on outside touch
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    public void setMuteMode(boolean mute) {
        if (player != null && playerView != null) {
            if (mute) {
                this.mute.setVisibility(View.GONE);
                unMute.setVisibility(View.VISIBLE);
            } else {
                unMute.setVisibility(View.GONE);
                this.mute.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateLockMode(boolean isLock) {
        if (player != null && playerView != null) {
            player.lockScreen(isLock);
            if (isLock) {
                playerView.hideController();
                unLock.setVisibility(View.VISIBLE);

            } else {
                playerView.showController();
                unLock.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showProgressBar(boolean visible) {
        if (visible)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

}
