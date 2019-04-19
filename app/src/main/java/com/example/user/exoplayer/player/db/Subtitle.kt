package com.example.user.exoplayer.player.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "subtitle",
        foreignKeys = [ForeignKey(entity = VideoUrl::class,
                parentColumns = ["id"],
                childColumns = ["videoId"],
                onDelete = CASCADE)])
@Parcelize
data class Subtitle(var videoId: Int,
               var title: String?,
               var subtitleUrl: String?) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
