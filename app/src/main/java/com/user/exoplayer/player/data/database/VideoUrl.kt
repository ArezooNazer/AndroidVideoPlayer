package com.user.exoplayer.player.data.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "video")
class VideoUrl(var videoUrl: String?) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
