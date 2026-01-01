package dev.ctsetera.ikaranpu.domain.repository

import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackError

interface ITrackRepository {
    suspend fun getTracks(): Result<List<Track>, TrackError>

    suspend fun getDraftTracks(): Result<List<Track>, TrackError>

    suspend fun getTrack(trackId: Long): Result<Track, TrackError>

    suspend fun addTrack(track: Track): Result<Long, TrackError>

    suspend fun updateTrack(track: Track): Result<Unit, TrackError>

    suspend fun deleteTrack(trackId: Long): Result<Unit, TrackError>
}