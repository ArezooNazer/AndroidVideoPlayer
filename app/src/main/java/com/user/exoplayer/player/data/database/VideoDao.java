package com.user.exoplayer.player.data.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface VideoDao {

    @Query("SELECT videoUrl FROM video")
    List<String> getAllUrls();

    @Query("SELECT * FROM Subtitle WHERE videoId LIKE :videoId")
    List<Subtitle> getAllSubtitles(int videoId);

    @Insert(onConflict = REPLACE)
    void insertAllVideoUrl(List<Video> urlList);

    @Insert(onConflict = REPLACE)
    void insertAllSubtitleUrl(List<Subtitle> subTitleList);


}
