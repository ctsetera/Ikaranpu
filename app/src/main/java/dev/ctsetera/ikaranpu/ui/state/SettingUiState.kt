package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.domain.model.AppSettings

data class SettingUiState(
    val isLoading: Boolean = true,
    val settings: AppSettings = AppSettings(),
)
