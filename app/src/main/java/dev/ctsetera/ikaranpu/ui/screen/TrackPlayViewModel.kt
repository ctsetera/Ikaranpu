package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackByTrackIdUseCase
import dev.ctsetera.ikaranpu.getMessageId
import dev.ctsetera.ikaranpu.ui.state.TrackPlayUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackPlayViewModel(
    private val getTrackByTrackIdUseCase: GetTrackByTrackIdUseCase,
    private val trackId: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackPlayUiState())
    val uiState: StateFlow<TrackPlayUiState> = _uiState

    init {
        playTrack()
    }

    private fun playTrack() {
        viewModelScope.launch {
            // Load Track
            getTrackByTrackIdUseCase(trackId)
                .onSuccess { track ->
                    _uiState.value = TrackPlayUiState(
                        isLoading = false,
                        track = track
                    )

                    // 再生

                    // UI更新
                    _uiState.update { state ->
                        state.copy(isPlaying = true)
                    }
                }.onFailure {
                    _uiState.value = TrackPlayUiState(
                        isLoading = false,
                        errorMessageId = it.getMessageId(),
                    )
                }
        }
    }

    override fun onCleared() {
        stopTrack()
    }

    private fun stopTrack() {
        viewModelScope.launch {
            // UI更新
            _uiState.update { state ->
                state.copy(isPlaying = false)
            }
        }
    }
}