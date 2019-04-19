package com.example.user.exoplayer.player.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.user.exoplayer.player.data.database.Subtitle;

import java.util.List;

public class VideoSource  implements Parcelable{

    private List<SingleVideo> videos;

    public VideoSource(List<SingleVideo> videos) {
        this.videos = videos;
    }

    public List<SingleVideo> getVideos() {
        return videos;
    }

    private VideoSource(Parcel in) {
        videos = in.createTypedArrayList(SingleVideo.CREATOR);
    }

    public static final Creator<VideoSource> CREATOR = new Creator<VideoSource>() {
        @Override
        public VideoSource createFromParcel(Parcel in) {
            return new VideoSource(in);
        }

        @Override
        public VideoSource[] newArray(int size) {
            return new VideoSource[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(videos);
    }

    public static class SingleVideo implements Parcelable{
        private int videoType;
        private String url;
        private List<Subtitle> subtitleList;

        public SingleVideo(int videoType, String url, List<Subtitle> subtitleList) {
            this.videoType = videoType;
            this.url = url;
            this.subtitleList = subtitleList;
        }

        public int getVideoType() {
            return videoType;
        }

        public String getUrl() {
            return url;
        }

        public List<Subtitle> getSubtitleList() {
            return subtitleList;
        }

        SingleVideo(Parcel in) {
            videoType = in.readInt();
            url = in.readString();
            subtitleList = in.createTypedArrayList(Subtitle.CREATOR);
        }

        public static final Creator<SingleVideo> CREATOR = new Creator<SingleVideo>() {
            @Override
            public SingleVideo createFromParcel(Parcel in) {
                return new SingleVideo(in);
            }

            @Override
            public SingleVideo[] newArray(int size) {
                return new SingleVideo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(videoType);
            parcel.writeString(url);
            parcel.writeTypedList(subtitleList);
        }
    }
}
