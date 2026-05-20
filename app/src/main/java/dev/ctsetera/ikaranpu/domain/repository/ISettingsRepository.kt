package dev.ctsetera.ikaranpu.domain.repository

import dev.ctsetera.ikaranpu.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {
    fun getSettings(): Flow<AppSettings>

    suspend fun saveVolume(
        volume: Int,
    )
}