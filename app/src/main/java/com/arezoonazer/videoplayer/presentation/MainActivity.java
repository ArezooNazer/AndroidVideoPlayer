package com.arezoonazer.videoplayer.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.arezoonazer.videoplayer.R;
import com.arezoonazer.videoplayer.data.database.AppDatabase;
import com.arezoonazer.videoplayer.data.database.Subtitle;
import com.arezoonazer.videoplayer.data.database.Video;
import com.arezoonazer.videoplayer.data.model.VideoSource;
import com.arezoonazer.videoplayer.presentation.player.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase database;
    private List<Subtitle> subtitleList = new ArrayList<>();
    private TextView playMp4, playM3u8;
    private List<Video> videoUriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLayout();
        initializeDb();
        makeListOfUri();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database = null;
    }

    private void setLayout() {
        playMp4 = findViewById(R.id.mp4);
        playM3u8 = findViewById(R.id.m3u8);

        playMp4.setOnClickListener(view -> goToPlayerActivity(makeVideoSource(videoUriList, 0)));
        playM3u8.setOnClickListener(view -> goToPlayerActivity(makeVideoSource(videoUriList, 1)));
    }

    private void initializeDb() {
        database = AppDatabase.Companion.getDatabase(getApplicationContext());
    }

    private void makeListOfUri() {
        videoUriList.add(new Video("https://www.radiantmediaplayer.com/media/bbb-360p.mp4", Long.getLong("zero", 0)));
        videoUriList.add(new Video("https://5b44cf20b0388.streamlock.net:8443/vod/smil:bbb.smil/playlist.m3u8", Long.getLong("zero", 0)));

        subtitleList.add(new Subtitle(2, "German", "https://durian.blender.org/wp-content/content/subtitles/sintel_en.srt"));
        subtitleList.add(new Subtitle(2, "French", "https://durian.blender.org/wp-content/content/subtitles/sintel_fr.srt"));

        if (database.videoDao().getAllUrls().size() == 0) {
            database.videoDao().insertAllVideoUrl(videoUriList);
            database.videoDao().insertAllSubtitleUrl(subtitleList);
        }

    }

    private VideoSource makeVideoSource(List<Video> videos, int index) {
        setVideosWatchLength();
        List<VideoSource.SingleVideo> singleVideos = new ArrayList<>();
        for (int i = 0; i < videos.size(); i++) {

            singleVideos.add(i, new VideoSource.SingleVideo(
                    videos.get(i).getVideoUrl(),
                    database.videoDao().getAllSubtitles(i + 1),
                    videos.get(i).getWatchedLength())
            );

        }
        return new VideoSource(singleVideos, index);
    }

    private List<Video> setVideosWatchLength() {
        List<Video> videosInDb = database.videoDao().getVideos();
        for (int i = 0; i < videosInDb.size(); i++) {
            videoUriList.get(i).setWatchedLength(videosInDb.get(i).getWatchedLength());
        }
        return videoUriList;
    }


    //start player for result due to future features
    public void goToPlayerActivity(VideoSource videoSource) {
        int REQUEST_CODE = 1000;
        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
        intent.putExtra("videoSource", videoSource);
        startActivityForResult(intent, REQUEST_CODE);
    }

}
