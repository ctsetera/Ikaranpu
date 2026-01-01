package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.domain.model.Track

data class DraftListUiState(
    val isLoading: Boolean = true,
    val drafts: List<Track> = emptyList(),
    val errorMessageId: Int? = null,
)