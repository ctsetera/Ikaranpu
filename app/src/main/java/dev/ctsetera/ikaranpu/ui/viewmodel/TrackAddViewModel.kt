package dev.ctsetera.ikaranpu.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.domain.usecase.AddTrackUseCase
import dev.ctsetera.ikaranpu.getMessageId
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.stateholder.TrackEditorStateHolder
import dev.ctsetera.ikaranpu.ui.validation.TrackValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class TrackAddViewModel(
    private val addTrackUseCase: AddTrackUseCase,
    savedStateHandle: SavedStateHandle,
    trackValidator: TrackValidator = TrackValidator(),
) : ViewModel() {
    private val editor = TrackEditorStateHolder(savedStateHandle, trackValidator)

    val uiState = editor.uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    private var addTrackJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            addTrackUseCase.progressFlow.collect(editor::updateProgress)
        }
    }

    fun changeTrackName(trackName: String) = editor.changeTrackName(trackName)

    fun changeCharacterType(characterType: CharacterType) =
        editor.changeCharacterType(characterType)

    fun changeText(index: Int, text: String) = editor.changeText(index, text)

    fun removeText(index: Int) = editor.removeText(index)

    fun addText() = editor.addText()

    fun changeInterval(interval: String) = editor.changeInterval(interval)

    fun changePlayMode(playMode: PlayMode) = editor.changePlayMode(playMode)

    fun addTrack(isActive: Boolean) {
        if (addTrackJob?.isActive == true) return

        addTrackJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                editor.setSaving(true)

                if (!editor.validate(required = isActive)) {
                    editor.setSaving(false)
                    return@launch
                }

                val state = uiState.value
                addTrackUseCase(
                    trackName = state.trackName,
                    characterType = state.characterType,
                    textList = state.textList,
                    interval = state.interval.toIntOrNull() ?: 0,
                    playMode = state.playMode,
                    state = if (isActive) TrackState.PLAYABLE else TrackState.DRAFT,
                )
                    .onSuccess {
                        editor.markSaveCompleted()
                        _uiEvent.emit(
                            UiEvent.ShowToast(
                                if (isActive) {
                                    R.string.track_save_success
                                } else {
                                    R.string.track_save_to_draft_success
                                }
                            )
                        )
                        _uiEvent.emit(UiEvent.Success)
                    }
                    .onFailure {
                        _uiEvent.emit(UiEvent.ShowToast(it.getMessageId()))
                        editor.setSaving(false)
                    }
            } finally {
                addTrackJob = null
            }
        }
    }

    fun cancelAddTrack() {
        if (uiState.value.isSaveCompleted) return

        addTrackJob?.cancel()
        addTrackJob = null
        editor.cancelSaving()
    }

    override fun onCleared() {
        cancelAddTrack()
        super.onCleared()
    }
}
