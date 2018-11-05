package com.example.player.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {VideoUrl.class, SubtitleUrl.class} , version = 2)
public abstract class UrlDatabase extends RoomDatabase {

    public abstract UrlDao urlDao();

}
