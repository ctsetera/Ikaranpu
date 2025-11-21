package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ctsetera.ikaranpu.domain.usecase.GetDraftListUseCase
import dev.ctsetera.ikaranpu.ui.state.TrackListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DraftViewModel : ViewModel() {

    private val getDraftListUseCase = GetDraftListUseCase()

    private val _uiState = MutableStateFlow(TrackListUiState())
    val uiState: StateFlow<TrackListUiState> = _uiState

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            val tracks = getDraftListUseCase()
            _uiState.value = TrackListUiState(
                isLoading = false,
                tracks = tracks
            )
        }
    }
}