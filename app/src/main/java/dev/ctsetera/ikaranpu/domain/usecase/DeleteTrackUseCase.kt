package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.TrackError
import dev.ctsetera.ikaranpu.domain.repository.ITrackRepository

class DeleteTrackUseCase(private val repository: ITrackRepository) {
    suspend operator fun invoke(trackId: Long): Result<Unit, TrackError> {
        return repository.deleteTrack(trackId)
    }
}