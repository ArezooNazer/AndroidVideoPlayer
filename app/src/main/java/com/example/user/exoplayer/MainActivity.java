package com.example.user.exoplayer;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.user.exoplayer.player.data.VideoSource;
import com.example.user.exoplayer.player.data.database.Subtitle;
import com.example.user.exoplayer.player.data.database.AppDatabase;
import com.example.user.exoplayer.player.data.database.VideoUrl;
import com.example.user.exoplayer.player.ui.PlayerActivity;
import com.google.android.exoplayer2.C;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public AppDatabase urlDatabase;
    private List<Subtitle> subtitleList = new ArrayList<>();

    /***********************************************************
     list of sample videos and multiple subtitles ( saved in db)
     ***********************************************************/

    private List<VideoUrl> videoUriList = new ArrayList<>();

    private void initializeDb() {
        urlDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "APP_DB")
                .allowMainThreadQueries()
                .build();
    }

    private void makeListOfUri(SourceListener sourceListener) {
        videoUriList.add(new VideoUrl("https:http://www.storiesinflight.com/js_videosub/jellies.mp4"));
        videoUriList.add(new VideoUrl("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"));

        subtitleList.add(new Subtitle(1, "English", "https://durian.blender.org/wp-content/content/subtitles/sintel_en.srt"));
        subtitleList.add(new Subtitle(2, "Farsi", "https://download.blender.org/durian/subs/sintel_fa.srt"));
        subtitleList.add(new Subtitle(2, "English", "https://durian.blender.org/wp-content/content/subtitles/sintel_en.srt"));
        subtitleList.add(new Subtitle(2, "French", "https://durian.blender.org/wp-content/content/subtitles/sintel_fr.srt"));

        if (urlDatabase.urlDao().getAllUrls().size() == 0) {
            urlDatabase.urlDao().insertAllVideoUrl(videoUriList);
            urlDatabase.urlDao().insertAllSubtitleUrl(subtitleList);
        }

        VideoSource videoSource = makeVideoSource(videoUriList);
        sourceListener.success(videoSource);
    }

    private VideoSource makeVideoSource(List<VideoUrl> videos) {
        List<VideoSource.SingleVideo> singleVideos = new ArrayList<>();
        List<Subtitle> subtitles;
        for (int i = 0; i < videos.size(); i++) {

            subtitles = urlDatabase.urlDao().getAllSubtitles(i);
            singleVideos.add(i, new VideoSource.SingleVideo(
                    videos.get(i).getVideoUrl(),
                    subtitles == null ? new ArrayList<>() : subtitles)
            );

        }
        return new VideoSource(singleVideos, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeDb();
        makeListOfUri(this::goToPlayerActivity);

    }

    public void goToPlayerActivity(VideoSource videoSource) {
        int REQUEST_CODE = 1000;
        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
        intent.putExtra("videoSource", videoSource);
        startActivityForResult(intent, REQUEST_CODE);
    }

}
