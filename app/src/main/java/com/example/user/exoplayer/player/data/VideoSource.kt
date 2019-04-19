package com.example.user.exoplayer.player.data

import android.os.Parcelable
import com.example.user.exoplayer.player.db.Subtitle
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoSource constructor(
        var video: List<SingleVideo>? = null
) : Parcelable {


    @Parcelize
    data class SingleVideo constructor(
            var videoType: Int? = 0,
            var url: String? = null,
            var subtitles: List<Subtitle>? = null
    ) : Parcelable

}