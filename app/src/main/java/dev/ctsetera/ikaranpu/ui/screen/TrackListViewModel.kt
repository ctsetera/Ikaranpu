package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.domain.usecase.DeleteTrackUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackListUseCase
import dev.ctsetera.ikaranpu.getMessageId
import dev.ctsetera.ikaranpu.ui.state.TrackListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackListViewModel(
    private val getTrackListUseCase: GetTrackListUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackListUiState())
    val uiState: StateFlow<TrackListUiState> = _uiState

    fun loadTracks() = viewModelScope.launch(Dispatchers.IO) {
        getTrackListUseCase()
            .onSuccess { tracks ->
                _uiState.value = TrackListUiState(
                    isLoading = false,
                    tracks = tracks
                )
            }
            .onFailure {
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
                _uiState.value = TrackListUiState(
                    errorMessageId = it.getMessageId(),
                )
            }
    }
}