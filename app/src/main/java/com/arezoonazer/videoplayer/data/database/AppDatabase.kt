package com.arezoonazer.videoplayer.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Video::class, Subtitle::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        private val sLock = Any()

        fun getDatabase(context: Context): AppDatabase {
            synchronized(sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "PLAYER_DB")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                }
                return INSTANCE!!
            }
        }
    }


}
