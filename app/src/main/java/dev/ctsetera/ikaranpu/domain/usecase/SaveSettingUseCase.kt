package dev.ctsetera.ikaranpu.domain.usecase

import dev.ctsetera.ikaranpu.data.repository.SettingsRepository

class SaveSettingUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(
        volume: Int,
    ) {
        repository.saveVolume(
            volume = volume,
        )
    }

    suspend operator fun invoke(
        checkPreRelease: Boolean,
    ) {
        repository.saveCheckPreRelease(checkPreRelease)
    }

    suspend operator fun invoke(
        postponedAtMillis: Long,
        version: String,
    ) {
        repository.saveUpdatePostponed(
            postponedAtMillis = postponedAtMillis,
            version = version,
        )
    }
}
