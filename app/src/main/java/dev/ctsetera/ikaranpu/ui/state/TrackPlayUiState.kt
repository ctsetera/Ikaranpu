package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.domain.model.Track

data class TrackPlayUiState(
    val isLoading: Boolean = true,
    val isPlaying: Boolean = false,
    val track: Track? = null,
    val errorMessageId: Int? = null,
)