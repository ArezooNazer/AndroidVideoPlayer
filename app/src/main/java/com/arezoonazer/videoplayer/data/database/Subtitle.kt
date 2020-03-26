package com.arezoonazer.videoplayer.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "subtitle",
        foreignKeys = [ForeignKey(entity = Video::class,
                parentColumns = ["id"],
                childColumns = ["videoId"],
                onDelete = CASCADE)])

class Subtitle(var videoId: Int = 0,
               var title: String? = null,
               var subtitleUrl: String? = null) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0


}
