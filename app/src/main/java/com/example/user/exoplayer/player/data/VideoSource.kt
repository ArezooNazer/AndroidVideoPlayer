package com.example.user.exoplayer.player.data

import android.os.Parcelable
import com.example.user.exoplayer.player.data.database.Subtitle

import kotlinx.android.parcel.Parcelize


@Parcelize
data class VideoSource constructor(
        var videos: List<SingleVideo>? = null,
        var selectedSourceIndex: Int = 0
) : Parcelable {

    @Parcelize
    data class SingleVideo(var videoType: Int? = 0,
                           var url: String? = null,
                           var subtitles: List<Subtitle>? = null
    ) : Parcelable
}

