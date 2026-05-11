package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.domain.usecase.DeleteTrackUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetDraftListUseCase
import dev.ctsetera.ikaranpu.getMessageId
import dev.ctsetera.ikaranpu.ui.UiEvent
import dev.ctsetera.ikaranpu.ui.state.DraftListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DraftViewModel(
    private val getDraftListUseCase: GetDraftListUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DraftListUiState())
    val uiState: StateFlow<DraftListUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        loadTracks()
    }

    fun loadTracks() = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = DraftListUiState(
            isLoading = true,
            drafts = emptyList(),
        )

        getDraftListUseCase()
            .onSuccess { drafts ->
                _uiState.value = DraftListUiState(
                    isLoading = false,
                    drafts = drafts
                )
            }
            .onFailure {
                _uiEvent.emit(UiEvent.ShowToast(it.getMessageId()))
                _uiState.value = DraftListUiState(
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
                    state.copy(drafts = state.drafts.filter { it.trackId != trackId })
                }
            }
            .onFailure {
                _uiEvent.emit(UiEvent.ShowToast(it.getMessageId()))
                _uiState.value = DraftListUiState(
                    errorMessageId = it.getMessageId(),
                )
            }
    }
}