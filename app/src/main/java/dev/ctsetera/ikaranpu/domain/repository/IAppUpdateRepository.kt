package dev.ctsetera.ikaranpu.domain.repository

import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.AppRelease
import dev.ctsetera.ikaranpu.domain.model.Error

interface IAppUpdateRepository {
    suspend fun getLatestRelease(includePreRelease: Boolean): Result<AppRelease?, Error>
}
