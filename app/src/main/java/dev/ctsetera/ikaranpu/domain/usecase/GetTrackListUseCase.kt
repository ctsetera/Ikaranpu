package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackError
import dev.ctsetera.ikaranpu.domain.repository.ITrackRepository

class GetTrackListUseCase(private val repository: ITrackRepository) {
    suspend operator fun invoke(): Result<List<Track>, TrackError> {
        return repository.getTracks()
    }
}