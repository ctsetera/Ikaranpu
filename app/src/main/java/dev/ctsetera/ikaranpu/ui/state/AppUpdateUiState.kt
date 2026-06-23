package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.domain.model.AppRelease

data class AppUpdateUiState(
    val availableRelease: AppRelease? = null,
)
