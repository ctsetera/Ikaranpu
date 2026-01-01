package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.domain.model.Track

data class TrackAddUiState(
    val isProcessing: Boolean = true,
    val isSuccess: Boolean = false,
    val track: Track? = null,
    val errorMessageId: Int? = null,
)