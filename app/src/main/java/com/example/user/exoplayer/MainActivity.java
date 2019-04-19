package com.example.user.exoplayer;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.user.exoplayer.player.data.VideoSource;
import com.example.user.exoplayer.player.db.Subtitle;
import com.example.user.exoplayer.player.db.UrlDatabase;
import com.example.user.exoplayer.player.db.VideoUrl;
import com.example.user.exoplayer.player.ui.PlayerActivity;
import com.google.android.exoplayer2.C;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static UrlDatabase urlDatabase;
    private List<Subtitle> subtitleList = new ArrayList<>();

    /***********************************************************
     list of sample videos and multiple subtitles ( saved in db)
     ***********************************************************/

    private List<VideoUrl> videoUriList = new ArrayList<>();

    private void initializeDb(SourceListener sourceListener) {
        urlDatabase = Room.databaseBuilder(getApplicationContext(), UrlDatabase.class, "URL_DB")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        makeListOfUri(sourceListener);
    }

    private void makeListOfUri(SourceListener sourceListener) {

        videoUriList.add(new VideoUrl("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"));
        videoUriList.add(new VideoUrl("http://cdn.theoplayer.com/video/big_buck_bunny/big_buck_bunny_metadata.m3u8"));
        videoUriList.add(new VideoUrl("http://www.storiesinflight.com/js_videosub/jellies.mp4"));

        subtitleList.add(new Subtitle(1, "English", "https://durian.blender.org/wp-content/content/subtitles/sintel_en.srt"));

        subtitleList.add(new Subtitle(2, "Farsi", "https://download.blender.org/durian/subs/sintel_fa.srt"));
        subtitleList.add(new Subtitle(2, "English", "https://durian.blender.org/wp-content/content/subtitles/sintel_en.srt"));
        subtitleList.add(new Subtitle(2, "French", "https://durian.blender.org/wp-content/content/subtitles/sintel_fr.srt"));


        subtitleList.add(new Subtitle(3, "English", "http://www.storiesinflight.com/js_videosub/jellies.srt"));

        if (urlDatabase.urlDao().getAllUrls().size() == 0) {
            urlDatabase.urlDao().insertAllVideoUrl(videoUriList);
            urlDatabase.urlDao().insertAllSubtitleUrl(subtitleList);
        }
        sourceListener.success(makeVideoSource(videoUriList));
    }

    private VideoSource makeVideoSource(List<VideoUrl> videos) {
        List<VideoSource.SingleVideo> singleVideos = new ArrayList<>();
        List<Subtitle> subtitles;
        for (int i = 0; i < videos.size(); i++) {

            subtitles = urlDatabase.urlDao().getAllSubtitles(i);
            singleVideos.add(i, new VideoSource.SingleVideo(
                    C.TYPE_HLS,
                    videos.get(i).getVideoUrl(),
                    subtitles == null ? new ArrayList<>() : subtitles)
            );

        }
        return new VideoSource(singleVideos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeDb(videoSource -> goToPlayerActivity(videoSource));

    }

    public void goToPlayerActivity(VideoSource videoSource) {
        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
        intent.putExtra("videoSource", videoSource);
        startActivity(intent);
    }


}
