package com.user.exoplayer.player.data.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

import android.arch.persistence.room.OnConflictStrategy.REPLACE

@Dao
interface VideoDao {

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
