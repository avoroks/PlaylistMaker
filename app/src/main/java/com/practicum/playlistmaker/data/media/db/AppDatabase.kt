package com.practicum.playlistmaker.data.media.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.data.media.db.dao.PlaylistDao
import com.practicum.playlistmaker.data.media.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.data.media.db.dao.TrackDao

@Database(
    version = 1,
    entities = [FavoriteTrackEntity::class, PlaylistEntity::class, TrackEntity::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}