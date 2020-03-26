package com.arezoonazer.videoplayer.data.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "video")
class Video(var videoUrl: String? = null,
            var watchedLength: Long? = null) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
