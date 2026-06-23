package dev.ctsetera.ikaranpu.data.repository

import dev.ctsetera.ikaranpu.data.local.cache.AppSettingsDataStore
import dev.ctsetera.ikaranpu.domain.model.AppSettings
import dev.ctsetera.ikaranpu.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val store: AppSettingsDataStore) : ISettingsRepository {
    override fun getSettings(): Flow<AppSettings> {
        return store.getSettings()
    }

    override suspend fun saveVolume(volume: Int) {
        store.saveVolume(
            volume = volume,
        )
    }

    override suspend fun saveCheckPreRelease(checkPreRelease: Boolean) {
        store.saveCheckPreRelease(checkPreRelease)
    }

    override suspend fun saveUpdatePostponed(
        postponedAtMillis: Long,
        version: String,
    ) {
        store.saveUpdatePostponed(
            postponedAtMillis = postponedAtMillis,
            version = version,
        )
    }
}
