package com.example.user.exoplayer.player.data.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

import android.arch.persistence.room.ForeignKey.CASCADE
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "subtitle",
        foreignKeys = [ForeignKey(entity = VideoUrl::class,
                parentColumns = ["id"],
                childColumns = ["videoId"],
                onDelete = CASCADE)])

class Subtitle(var videoId: Int = 0,
               var title: String? = null,
               var subtitleUrl: String? = null) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0


}
