package dev.ctsetera.ikaranpu.data.repository

import dev.ctsetera.ikaranpu.data.local.cache.AppSettingsDataStore
import dev.ctsetera.ikaranpu.domain.model.AppSettings
import dev.ctsetera.ikaranpu.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val store: AppSettingsDataStore) : ISettingsRepository {
    override fun getSettings(): Flow<AppSettings> {
        return store
            .getVolume()
            .map { volume ->
                AppSettings(
                    volume = volume,
                )
            }
    }

    override suspend fun saveVolume(volume: Int) {
        store.saveVolume(
            volume = volume,
        )
    }
}