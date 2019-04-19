package com.example.user.exoplayer.player.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

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
public class Subtitle implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int videoId;
    private String title;
    private String subtitleUrl;

    public Subtitle(int videoId, String title, String subtitleUrl) {

        this.videoId = videoId;
        this.title = title;
        this.subtitleUrl = subtitleUrl;
    }


    private Subtitle(Parcel in) {
        id = in.readInt();
        videoId = in.readInt();
        title = in.readString();
        subtitleUrl = in.readString();
    }

    public static final Creator<Subtitle> CREATOR = new Creator<Subtitle>() {
        @Override
        public Subtitle createFromParcel(Parcel in) {
            return new Subtitle(in);
        }

        @Override
        public Subtitle[] newArray(int size) {
            return new Subtitle[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(videoId);
        parcel.writeString(title);
        parcel.writeString(subtitleUrl);
    }
}
