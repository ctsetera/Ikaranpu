package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackError
import dev.ctsetera.ikaranpu.domain.repository.ITrackRepository

class UpdateTrackUseCase(private val repository: ITrackRepository) {
    suspend operator fun invoke(track: Track): Result<Unit, TrackError> {
        return repository.updateTrack(track)
    }
}