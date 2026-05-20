package dev.ctsetera.ikaranpu.domain.usecase

import dev.ctsetera.ikaranpu.data.repository.SettingsRepository
import dev.ctsetera.ikaranpu.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<AppSettings> {
        return repository.getSettings()
    }
}