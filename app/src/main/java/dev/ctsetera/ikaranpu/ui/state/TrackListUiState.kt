package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.domain.model.Track

data class TrackListUiState(
    val isLoading: Boolean = true,
    val tracks: List<Track> = emptyList(),
    val errorMessageId: Int? = null,
)