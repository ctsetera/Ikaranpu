package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackListUseCase
import dev.ctsetera.ikaranpu.ui.state.TrackListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackListViewModel : ViewModel() {

    private val getTrackListUseCase = GetTrackListUseCase()

    private val _uiState = MutableStateFlow(TrackListUiState())
    val uiState: StateFlow<TrackListUiState> = _uiState

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            val tracks = getTrackListUseCase() // ← List<Track>
            _uiState.value = TrackListUiState(
                isLoading = false,
                tracks = tracks
            )
        }
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            // UI更新
            _uiState.update { state ->
                state.copy(tracks = state.tracks.filter { it.trackId != trackId })
            }
        }
    }
}