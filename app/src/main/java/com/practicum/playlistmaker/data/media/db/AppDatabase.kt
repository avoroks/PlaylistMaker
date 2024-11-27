package com.practicum.playlistmaker.data.media.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.data.media.db.dao.TrackDao

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}