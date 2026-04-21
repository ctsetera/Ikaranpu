package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.repository.ITrackRepository

class GetTrackByTrackIdUseCase(private val repository: ITrackRepository) {
    suspend operator fun invoke(trackId: Long): Result<Track, Error> {
        return repository.getTrack(trackId)
    }
}