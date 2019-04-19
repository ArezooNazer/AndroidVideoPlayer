package com.example.user.exoplayer.player.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {VideoUrl.class, Subtitle.class} , version = 3)
public abstract class UrlDatabase extends RoomDatabase {

    public abstract UrlDao urlDao();

}
