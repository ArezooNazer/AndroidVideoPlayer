package com.example.player.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "subtitle",
        foreignKeys = {
        @ForeignKey(
                entity = VideoUrl.class,
                parentColumns = "id",
                childColumns = "videoId",
                onDelete = CASCADE
        )
})
public class SubtitleUrl {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int videoId;
    private String title;
    private String subtitleUrl;

    public SubtitleUrl(int videoId, String title, String subtitleUrl) {

        this.videoId = videoId;
        this.title = title;
        this.subtitleUrl = subtitleUrl;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVideoId() {
        return videoId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getSubtitleUrl() {
        return subtitleUrl;
    }

    public void setSubtitleUrl(String subtitleUrl) {
        this.subtitleUrl = subtitleUrl;
    }
}
