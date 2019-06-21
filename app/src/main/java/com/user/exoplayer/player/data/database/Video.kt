package com.user.exoplayer.player.data.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.math.BigInteger

@Entity(tableName = "video")
class Video(var videoUrl: String? = null,
            var watchedLength: Long? = null) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
