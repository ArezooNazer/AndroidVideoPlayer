package com.arezoonazer.videoplayer.presentation.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.arezoonazer.videoplayer.R;
import com.arezoonazer.videoplayer.app.PlayerApplication;
import com.arezoonazer.videoplayer.data.database.AppDatabase;
import com.arezoonazer.videoplayer.data.model.VideoSource;
import com.arezoonazer.videoplayer.presentation.player.util.PlayerController;
import com.arezoonazer.videoplayer.presentation.player.util.VideoPlayer;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, PlayerController {

    private static final String TAG = "PlayerActivity";
    private PlayerView playerView;
    private VideoPlayer player;
    private ImageButton mute, unMute, subtitle, setting, lock, unLock, nextBtn, preBtn, retry, back;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private VideoSource videoSource;
    private AudioManager mAudioManager;
    private boolean disableBackPress = false;

    /***********************************************************
     Handle audio on different events
     ***********************************************************/
    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            if (player != null)
                                //  player.getPlayer().setPlayWhenReady(true);
                                break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                            if (player != null)
                                player.getPlayer().setPlayWhenReady(false);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost audio focus, probably "permanently"
                            // Lost audio focus, but will gain it back (shortly), so note whether
                            // playback should resume
                            if (player != null)
                                player.getPlayer().setPlayWhenReady(false);
                            break;
                    }
                }
            };


    /***********************************************************
     Activity lifecycle
     ***********************************************************/
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getDataFromIntent();
        setupLayout();
        initSource();
    }

    private void getDataFromIntent() {
        videoSource = getIntent().getParcelableExtra("videoSource");
    }

    private void setupLayout() {
        playerView = findViewById(R.id.demo_player_view);
        progressBar = findViewById(R.id.progress_bar);

        mute = findViewById(R.id.btn_mute);
        unMute = findViewById(R.id.btn_unMute);
        subtitle = findViewById(R.id.btn_subtitle);
        setting = findViewById(R.id.btn_settings);
        lock = findViewById(R.id.btn_lock);
        unLock = findViewById(R.id.btn_unLock);
        nextBtn = findViewById(R.id.btn_next);
        preBtn = findViewById(R.id.btn_prev);
        retry = findViewById(R.id.retry_btn);
        back = findViewById(R.id.btn_back);

        //optional setting
        playerView.getSubtitleView().setVisibility(View.GONE);

        mute.setOnClickListener(this);
        unMute.setOnClickListener(this);
        subtitle.setOnClickListener(this);
        setting.setOnClickListener(this);
        lock.setOnClickListener(this);
        unLock.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        preBtn.setOnClickListener(this);
        retry.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void initSource() {

        if (videoSource.getVideos() == null) {
            Toast.makeText(this, "can not play video", Toast.LENGTH_SHORT).show();
            return;
        }

        player = new VideoPlayer(playerView, getApplicationContext(), videoSource, this);

        checkIfVideoHasSubtitle();

        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        //optional setting
        playerView.getSubtitleView().setVisibility(View.GONE);
        player.seekToOnDoubleTap();

        playerView.setControllerVisibilityListener(visibility ->
        {
            Log.i(TAG, "onVisibilityChange: " + visibility);
            if (player.isLock())
                playerView.hideController();

            back.setVisibility(visibility == View.VISIBLE && !player.isLock() ? View.VISIBLE : View.GONE);
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (player != null)
            player.resumePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if (player != null)
            player.resumePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null)
            player.releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
            mAudioManager = null;
        }
        if (player != null) {
            player.releasePlayer();
            player = null;
        }
        PlayerApplication.getRefWatcher(this).watch(this);
    }

    @Override
    public void onBackPressed() {
        if (disableBackPress)
            return;

        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUi();
    }

    @Override
    public void onClick(View view) {
        int controllerId = view.getId();

        switch (controllerId) {
            case R.id.btn_mute:
                player.setMute(true);
                break;
            case R.id.btn_unMute:
                player.setMute(false);
                break;
            case R.id.btn_settings:
                player.setSelectedQuality(this);
                break;
            case R.id.btn_subtitle:
                prepareSubtitles();
                break;
            case R.id.btn_lock:
                updateLockMode(true);
                break;
            case R.id.btn_unLock:
                updateLockMode(false);
                break;
            case R.id.exo_rew:
                player.seekToSelectedPosition(0, true);
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.retry_btn:
                initSource();
                showProgressBar(true);
                showRetryBtn(false);
                break;
            case R.id.btn_next:
                player.seekToNext();
                checkIfVideoHasSubtitle();
                break;
            case R.id.btn_prev:
                player.seekToPrevious();
                checkIfVideoHasSubtitle();
                break;
            default:
                break;
        }
    }

    /***********************************************************
     UI config
     ***********************************************************/
    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void showSubtitle(boolean show) {

        if (player == null || playerView.getSubtitleView() == null)
            return;

        if (!show) {
            playerView.getSubtitleView().setVisibility(View.GONE);
            return;
        }

        alertDialog.dismiss();
        playerView.getSubtitleView().setVisibility(View.VISIBLE);
    }

    @Override
    public void changeSubtitleBackground() {
        CaptionStyleCompat captionStyleCompat = new CaptionStyleCompat(Color.YELLOW, Color.TRANSPARENT, Color.TRANSPARENT,
                CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW, Color.LTGRAY, null);
        playerView.getSubtitleView().setStyle(captionStyleCompat);
    }

    private boolean checkIfVideoHasSubtitle(){
        if (player.getCurrentVideo().getSubtitles() == null ||
                player.getCurrentVideo().getSubtitles().size() == 0) {
            subtitle.setImageResource(R.drawable.exo_no_subtitle_btn);
            return true;
        }

        subtitle.setImageResource(R.drawable.exo_subtitle_btn);
        return false;
    }

    private void prepareSubtitles() {
        if (player == null || playerView.getSubtitleView() == null)
            return;

        if (checkIfVideoHasSubtitle()) {
            Toast.makeText(this, getString(R.string.no_subtitle), Toast.LENGTH_SHORT).show();
            return;
        }

        player.pausePlayer();
        showSubtitleDialog();

    }

    private void showSubtitleDialog() {
        //init subtitle dialog
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        recyclerView.setAdapter(new SubtitleAdapter(player.getCurrentVideo().getSubtitles(), player));

        for (int i = 0; i < player.getCurrentVideo().getSubtitles().size(); i++) {
            Log.d("subtitle", "showSubtitleDialog: " + player.getCurrentVideo().getSubtitles().get(i).getTitle());
        }

        TextView noSubtitle = view.findViewById(R.id.no_subtitle_text_view);
        noSubtitle.setOnClickListener(view1 -> {
            if (playerView.getSubtitleView().getVisibility() == View.VISIBLE)
                showSubtitle(false);
            alertDialog.dismiss();
            player.resumePlayer();
        });

        Button cancelDialog = view.findViewById(R.id.cancel_dialog_btn);
        cancelDialog.setOnClickListener(view1 -> {
            alertDialog.dismiss();
            player.resumePlayer();
        });

        // to prevent dialog box from getting dismissed on outside touch
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
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

    @Override
    public void showProgressBar(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void updateLockMode(boolean isLock) {
        if (player == null || playerView == null)
            return;

        player.lockScreen(isLock);

        if (isLock) {
            disableBackPress = true;
            playerView.hideController();
            unLock.setVisibility(View.VISIBLE);
            return;
        }

        disableBackPress = false;
        playerView.showController();
        unLock.setVisibility(View.GONE);

    }

    @Override
    public void showRetryBtn(boolean visible) {
        retry.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void audioFocus() {
        mAudioManager.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void setVideoWatchedLength() {
        AppDatabase.Companion.getDatabase(getApplicationContext()).videoDao().
                updateWatchedLength(player.getCurrentVideo().getUrl(), player.getWatchedLength());
    }

    @Override
    public void videoEnded() {
        AppDatabase.Companion.getDatabase(getApplicationContext()).videoDao().
                updateWatchedLength(player.getCurrentVideo().getUrl(), 0);
        player.seekToNext();
    }

    @Override
    public void disableNextButtonOnLastVideo(boolean disable) {
        if(disable){
            nextBtn.setImageResource(R.drawable.exo_disable_next_btn);
            nextBtn.setEnabled(false);
            return;
        }

        nextBtn.setImageResource(R.drawable.exo_next_btn);
        nextBtn.setEnabled(true);
    }
}
