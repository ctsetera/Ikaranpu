package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.AppRelease
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.repository.IAppUpdateRepository

class GetLatestAppReleaseUseCase(
    private val repository: IAppUpdateRepository,
) {
    suspend operator fun invoke(includePreRelease: Boolean): Result<AppRelease?, Error> {
        return repository.getLatestRelease(includePreRelease)
    }
}
