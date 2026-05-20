package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackByTrackIdUseCase
import dev.ctsetera.ikaranpu.domain.usecase.PlayTrackUseCase
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
    private val playTrackUseCase: PlayTrackUseCase,
    private val trackId: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackPlayUiState())
    val uiState: StateFlow<TrackPlayUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        getTrack()

        playTrack()
    }

    private fun getTrack() = viewModelScope.launch(Dispatchers.IO) {
        getTrackByTrackIdUseCase(trackId)
            .onSuccess { track ->
                _uiState.update { state ->
                    state.copy(
                        track = track,
                    )
                }
            }
            .onFailure {
                _uiEvent.emit(UiEvent.ShowToast(it.getMessageId()))
                _uiState.update { state ->
                    state.copy(
                        errorMessageId = it.getMessageId(),
                    )
                }
            }
    }

    private fun playTrack() {
        // 多重再生防止
        if (_uiState.value.isPlaying) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            when (
                val result = playTrackUseCase(trackId)
            ) {
                is Ok -> {
                    _uiState.value = _uiState.value.copy(
                        isPlaying = true,
                    )
                }

                is Err -> {
                    _uiEvent.emit(UiEvent.ShowToast(result.error.getMessageId()))
                    _uiState.value = TrackPlayUiState(
                        errorMessageId = result.error.getMessageId(),
                    )
                }
            }
        }
    }

    /**
     * 再生停止
     */
    fun stop() = viewModelScope.launch(Dispatchers.IO) {
        when (
            val result = playTrackUseCase.stop()
        ) {

            is Ok -> {
                _uiState.value = _uiState.value.copy(
                    isPlaying = false,
                )
            }

            is Err -> {
                _uiEvent.emit(UiEvent.ShowToast(result.error.getMessageId()))
                _uiState.value = _uiState.value.copy(
                    errorMessageId = result.error.getMessageId(),
                )
            }
        }
    }

    override fun onCleared() {
        stop()

        super.onCleared()
    }
}