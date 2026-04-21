package dev.ctsetera.ikaranpu.domain.repository

import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.Track

interface ITrackRepository {
    suspend fun getTracks(): Result<List<Track>, Error>

    suspend fun getDraftTracks(): Result<List<Track>, Error>

    suspend fun getTrack(trackId: Long): Result<Track, Error>

    suspend fun addTrack(track: Track): Result<Long, Error>

    suspend fun updateTrack(track: Track): Result<Unit, Error>

    suspend fun deleteTrack(trackId: Long): Result<Unit, Error>
}