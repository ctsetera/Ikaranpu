package dev.ctsetera.ikaranpu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.domain.usecase.DeleteTrackUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackListUseCase
import dev.ctsetera.ikaranpu.getMessageId
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.state.TrackListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackListViewModel(
    private val getTrackListUseCase: GetTrackListUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackListUiState())
    val uiState: StateFlow<TrackListUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        loadTracks()
    }

    fun loadTracks() = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = TrackListUiState(
            isLoading = true,
            tracks = emptyList(),
        )

        getTrackListUseCase()
            .onSuccess { tracks ->
                _uiState.value = TrackListUiState(
                    isLoading = false,
                    tracks = tracks
                )
            }
            .onFailure {
                _uiEvent.emit(UiEvent.ShowToast(it.getMessageId()))
                _uiState.value = TrackListUiState(
                    isLoading = false,
                    errorMessageId = it.getMessageId(),
                )
            }
    }

    fun deleteTrack(trackId: Long) = viewModelScope.launch(Dispatchers.IO) {
        // データ削除
        deleteTrackUseCase(trackId)
            .onSuccess {
                // UI更新
                _uiState.update { state ->
                    state.copy(tracks = state.tracks.filter { it.trackId != trackId })
                }
            }
            .onFailure {
                _uiEvent.emit(UiEvent.ShowToast(it.getMessageId()))
                _uiState.value = TrackListUiState(
                    errorMessageId = it.getMessageId(),
                )
            }
    }
}