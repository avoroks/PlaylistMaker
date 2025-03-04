package com.practicum.playlistmaker.data.media.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.media.db.TrackEntity

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks ORDER BY createdAt DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("DELETE FROM tracks WHERE id = :id")
    suspend fun deleteTrack(id: Int)
}