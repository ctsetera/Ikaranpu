package dev.ctsetera.ikaranpu.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.TrackProgress
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.domain.usecase.AddTrackUseCase
import dev.ctsetera.ikaranpu.getMessageId
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.state.TrackAddUiState
import dev.ctsetera.ikaranpu.ui.validation.TrackValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackAddViewModel(
    private val addTrackUseCase: AddTrackUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val trackValidator: TrackValidator = TrackValidator(),
) : ViewModel() {

    companion object {
        private const val KEY_TRACK_NAME = "track_name"
        private const val KEY_CHARACTER_TYPE = "character_type"
        private const val KEY_TEXT_LIST = "text_list"
        private const val KEY_INTERVAL = "interval"
        private const val KEY_PLAY_MODE = "play_mode"
    }

    init {
        observeProgress()
    }

    private fun observeProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            addTrackUseCase.progressFlow.collect { progress ->
                when (progress) {
                    is TrackProgress.Downloaded -> {
                        if (progress.current == progress.total) {
                            _uiState.update { state ->
                                state.copy(
                                    dialogSowing = false,
                                    dialogProgressCurrent = 0,
                                    dialogProgressTotal = 10,
                                )
                            }
                        }
                    }

                    is TrackProgress.Downloading -> {
                        _uiState.update { state ->
                            state.copy(
                                dialogSowing = true,
                                dialogProgressCurrent = progress.current,
                                dialogProgressTotal = progress.total,
                            )
                        }
                    }

                    else -> {
                        _uiState.update { state ->
                            state.copy(
                                dialogSowing = false,
                                dialogProgressCurrent = 0,
                                dialogProgressTotal = 10,
                            )
                        }
                    }
                }
            }
        }
    }

    private val _uiState = MutableStateFlow(
        TrackAddUiState(
            trackName = savedStateHandle[KEY_TRACK_NAME] ?: "",
            characterType = savedStateHandle[KEY_CHARACTER_TYPE] ?: CharacterType.ZUNDAMON,
            textList = savedStateHandle[KEY_TEXT_LIST] ?: listOf(""),
            interval = savedStateHandle[KEY_INTERVAL] ?: "",
            playMode = savedStateHandle[KEY_PLAY_MODE] ?: PlayMode.NORMAL,
        )
    )
    val uiState: StateFlow<TrackAddUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    fun changeTrackName(trackName: String) {
        savedStateHandle[KEY_TRACK_NAME] = trackName
        _uiState.update {
            it.copy(
                trackName = trackName,
                validateTrackName = trackValidator.validateTrackName(trackName),
            )
        }
    }

    fun changeCharacterType(characterType: CharacterType) {
        savedStateHandle[KEY_CHARACTER_TYPE] = characterType
        _uiState.update { it.copy(characterType = characterType) }
    }

    fun changeTextListItem(index: Int, text: String) {
        val oldList = _uiState.value.textList
        val newList = oldList.toMutableList()

        newList[index] = text

        savedStateHandle[KEY_TEXT_LIST] = newList.toList()
        _uiState.update {
            it.copy(
                textList = newList.toList(),
                validateTextList = trackValidator.validateTextList(newList),
            )
        }
    }

    fun removeTextListItem(index: Int) {
        val oldList = _uiState.value.textList
        val newList = oldList.toMutableList()

        newList.removeAt(index)

        savedStateHandle[KEY_TEXT_LIST] = newList.toList()
        _uiState.update {
            it.copy(
                textList = newList.toList(),
                validateTextList = trackValidator.validateTextList(newList),
            )
        }
    }

    fun addTextListItem() {
        val oldList = _uiState.value.textList
        val newList = oldList.toMutableList()

        if (newList.size < 10) newList += ""

        savedStateHandle[KEY_TEXT_LIST] = newList.toList()
        _uiState.update {
            it.copy(
                textList = newList.toList(),
                validateTextList = trackValidator.validateTextList(newList),
            )
        }
    }

    fun changeInterval(interval: String) {
        savedStateHandle[KEY_INTERVAL] = interval
        _uiState.update {
            it.copy(
                interval = interval,
                validateInterval = trackValidator.validateInterval(interval),
            )
        }
    }

    fun changePlayMode(playMode: PlayMode) {
        savedStateHandle[KEY_PLAY_MODE] = playMode
        _uiState.update { it.copy(playMode = playMode) }
    }

    private var addTrackJob: Job? = null

    fun addTrack(
        isActive: Boolean,
    ) {
        // Coroutineの二重実行を防止
        if (addTrackJob?.isActive == true) return

        addTrackJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { state ->
                state.copy(isSaving = true)
            }

            if (!validateAll(isActive)) {
                _uiState.update { state ->
                    state.copy(isSaving = false)
                }
                return@launch
            }

            addTrackUseCase(
                trackName = _uiState.value.trackName,
                characterType = _uiState.value.characterType,
                textList = _uiState.value.textList,
                interval = _uiState.value.interval.toIntOrNull() ?: 0,
                playMode = _uiState.value.playMode,
                state = if (isActive) TrackState.PLAYABLE else TrackState.DRAFT,
            )
                .onSuccess {
                    _uiEvent.emit(UiEvent.ShowToast(if (isActive) R.string.track_save_success else R.string.track_save_to_draft_success))
                    _uiEvent.emit(UiEvent.Success)
                }
                .onFailure {
                    _uiEvent.emit(UiEvent.ShowToast(it.getMessageId()))
                    _uiState.update { state ->
                        state.copy(
                            errorMessageId = it.getMessageId(),
                            isSaving = false,
                        )
                    }
                }
            addTrackJob = null
        }
    }

    fun cancelAddTrack() {
        // 実行中のトラック更新処理をキャンセル
        addTrackJob?.cancel()
        addTrackJob = null

        // ダイアログを閉じる
        _uiState.update { state ->
            state.copy(
                dialogSowing = false,
                dialogProgressCurrent = 0,
                dialogProgressTotal = 10,
                isSaving = false,
            )
        }
    }

    private fun validateAll(isActive: Boolean): Boolean {
        val state = _uiState.value
        val result = trackValidator.validate(
            trackName = state.trackName,
            textList = state.textList,
            interval = state.interval,
            required = isActive,
        )

        _uiState.update {
            it.copy(
                validateTrackName = result.trackNameError,
                validateTextList = result.textListErrors,
                validateInterval = result.intervalError,
            )
        }

        return result.isValid
    }

    override fun onCleared() {
        cancelAddTrack()

        super.onCleared()
    }
}
