package com.example.user.exoplayer;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.user.exoplayer.player.data.VideoSource;
import com.example.user.exoplayer.player.data.database.Subtitle;
import com.example.user.exoplayer.player.data.database.AppDatabase;
import com.example.user.exoplayer.player.data.database.VideoUrl;
import com.example.user.exoplayer.player.ui.PlayerActivity;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase urlDatabase;
    private List<Subtitle> subtitleList = new ArrayList<>();
    private TextView playMp4, playM3u8;

    /***********************************************************
     list of sample videos and multiple subtitles ( saved in db)
     ***********************************************************/

    private List<VideoUrl> videoUriList = new ArrayList<>();

    private void initializeDb() {
        urlDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "APP_DB")
                .allowMainThreadQueries()
                .build();
    }

    private void makeListOfUri() {
        videoUriList.add(new VideoUrl("https://www.radiantmediaplayer.com/media/bbb-360p.mp4"));
        videoUriList.add(new VideoUrl("https://5b44cf20b0388.streamlock.net:8443/vod/smil:bbb.smil/playlist.m3u8"));

        subtitleList.add(new Subtitle(1, "English", "https://durian.blender.org/wp-content/content/subtitles/sintel_en.srt"));
        subtitleList.add(new Subtitle(1, "Farsi", "https://download.blender.org/durian/subs/sintel_fa.srt"));

        subtitleList.add(new Subtitle(2, "German", "https://durian.blender.org/wp-content/content/subtitles/sintel_en.srt"));
        subtitleList.add(new Subtitle(2, "French", "https://durian.blender.org/wp-content/content/subtitles/sintel_fr.srt"));

        if (urlDatabase.urlDao().getAllUrls().size() == 0) {
            urlDatabase.urlDao().insertAllVideoUrl(videoUriList);
            urlDatabase.urlDao().insertAllSubtitleUrl(subtitleList);
        }

    }

    private VideoSource makeVideoSource(List<VideoUrl> videos, int index) {
        List<VideoSource.SingleVideo> singleVideos = new ArrayList<>();
        for (int i = 0; i < videos.size(); i++) {

            singleVideos.add(i, new VideoSource.SingleVideo(
                    videos.get(i).getVideoUrl(),
                    urlDatabase.urlDao().getAllSubtitles(i + 1))
            );

        }
        return new VideoSource(singleVideos, index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLayout();
        // initialize stetho
        Stetho.initializeWithDefaults(this);
        initializeDb();
        makeListOfUri();
    }

    private void setLayout() {
        playMp4 = findViewById(R.id.mp4);
        playM3u8 = findViewById(R.id.m3u8);

        playMp4.setOnClickListener(view -> goToPlayerActivity(makeVideoSource(videoUriList, 0)));
        playM3u8.setOnClickListener(view -> goToPlayerActivity(makeVideoSource(videoUriList, 1)));
    }


    public void goToPlayerActivity(VideoSource videoSource) {
        int REQUEST_CODE = 1000;
        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
        intent.putExtra("videoSource", videoSource);
        startActivityForResult(intent, REQUEST_CODE);
    }

}
