package dev.ctsetera.ikaranpu.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.ctsetera.ikaranpu.data.local.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert
    suspend fun insert(user: TrackEntity): Long

    @Update
    suspend fun update(user: TrackEntity)

    @Query("DELETE FROM tracks WHERE track_id = :trackId")
    suspend fun deleteByTrackId(trackId: Long)

    @Query("SELECT * FROM tracks")
    suspend fun getAll(): List<TrackEntity>

    @Query("SELECT * FROM tracks where isActive = 1")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT * FROM tracks where isActive = 0")
    suspend fun getDraftTracks(): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE track_id = :trackId")
    suspend fun findByTrackId(trackId: Long): TrackEntity?
}