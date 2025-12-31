package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackByTrackIdUseCase
import dev.ctsetera.ikaranpu.ui.state.TrackPlayUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackPlayViewModel(private val trackId: Int) : ViewModel() {

    private val getTrackByTrackIdUseCase = GetTrackByTrackIdUseCase()

    private val _uiState = MutableStateFlow(TrackPlayUiState())
    val uiState: StateFlow<TrackPlayUiState> = _uiState

    init {
        playTrack()
    }

    private fun playTrack() {
        viewModelScope.launch {
            // Load Track
            val track = getTrackByTrackIdUseCase(trackId)
            _uiState.value = TrackPlayUiState(
                isLoading = false,
                track = track
            )

            // Play

            _uiState.update { state ->
                state.copy(isPlaying = true)
            }
        }
    }

    override fun onCleared() {
        stopTrack(trackId = 1)
    }

    private fun stopTrack(trackId: Int) {
        viewModelScope.launch {
            // UI更新
            _uiState.update { state ->
                state.copy(isPlaying = false)
            }
        }
    }
}