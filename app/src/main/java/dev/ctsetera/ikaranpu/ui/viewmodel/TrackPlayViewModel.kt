package dev.ctsetera.ikaranpu.ui.viewmodel

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class TrackPlayViewModel(
    private val getTrackByTrackIdUseCase: GetTrackByTrackIdUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    private val trackId: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackPlayUiState())
    val uiState: StateFlow<TrackPlayUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    private var playJob: Job? = null

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
        if (playJob?.isActive == true) {
            return
        }

        playJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { state ->
                state.copy(isPlaying = true)
            }

            try {
                when (
                    val result = playTrackUseCase(trackId)
                ) {
                    is Ok -> Unit

                    is Err -> {
                        _uiEvent.emit(UiEvent.ShowToast(result.error.getMessageId()))
                        _uiState.value = TrackPlayUiState(
                            errorMessageId = result.error.getMessageId(),
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } finally {
                playTrackUseCase.stop()
                playJob = null
                _uiState.update { state ->
                    state.copy(isPlaying = false)
                }
            }
        }
    }

    /**
     * 再生停止
     */
    fun stop() = viewModelScope.launch(Dispatchers.IO) {
        playJob?.cancel()
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
        playJob?.cancel()
        playJob = null
        playTrackUseCase.stop()

        super.onCleared()
    }
}
