package com.practicum.playlistmaker.data.media.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.media.db.TrackEntity

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM favorite_tracks WHERE id = :id")
    suspend fun deleteTrack(id: Int)

    @Query("SELECT * FROM favorite_tracks order by createdAt desc")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT id FROM favorite_tracks")
    suspend fun getIdTracks(): List<Int>
}