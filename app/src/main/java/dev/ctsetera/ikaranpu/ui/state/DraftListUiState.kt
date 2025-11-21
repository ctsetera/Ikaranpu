package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.domain.model.Track

data class DraftListUiState(
    val isLoading: Boolean = true,
    val tracks: List<Track> = emptyList(),
    val errorMessage: String? = null,
)