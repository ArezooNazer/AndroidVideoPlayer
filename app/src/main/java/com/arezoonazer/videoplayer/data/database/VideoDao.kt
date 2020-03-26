package com.arezoonazer.videoplayer.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface VideoDao {

    @get:Query("SELECT * FROM video")
    val videos: List<Video>

    @get:Query("SELECT videoUrl FROM video")
    val allUrls: List<String>

    @Query("SELECT * FROM Subtitle WHERE videoId LIKE :videoId")
    fun getAllSubtitles(videoId: Int): List<Subtitle>

    @Query("UPDATE video SET watchedLength = :watchedLength WHERE videoUrl = :url ")
    fun updateWatchedLength(url: String, watchedLength: Long)

    @Insert(onConflict = REPLACE)
    fun insertAllVideoUrl(urlList: List<Video>)

    @Insert(onConflict = REPLACE)
    fun insertAllSubtitleUrl(subTitleList: List<Subtitle>)


}
