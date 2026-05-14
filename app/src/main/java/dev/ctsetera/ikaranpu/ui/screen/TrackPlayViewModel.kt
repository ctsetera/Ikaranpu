package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackByTrackIdUseCase
import dev.ctsetera.ikaranpu.getMessageId
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.state.TrackPlayUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackPlayViewModel(
    private val getTrackByTrackIdUseCase: GetTrackByTrackIdUseCase,
    private val trackId: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackPlayUiState())
    val uiState: StateFlow<TrackPlayUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        playTrack()
    }

    private fun playTrack() = viewModelScope.launch(Dispatchers.IO) {
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
                _uiEvent.emit(UiEvent.ShowToast(it.getMessageId()))
                _uiState.value = TrackPlayUiState(
                    isLoading = false,
                    errorMessageId = it.getMessageId(),
                )
            }
    }

    override fun onCleared() {
        stopTrack()
    }

    private fun stopTrack() = viewModelScope.launch(Dispatchers.IO) {
        // UI更新
        _uiState.update { state ->
            state.copy(isPlaying = false)
        }
    }
}