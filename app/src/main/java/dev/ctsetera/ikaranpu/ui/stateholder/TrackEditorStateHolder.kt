package dev.ctsetera.ikaranpu.ui.stateholder

import androidx.lifecycle.SavedStateHandle
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackProgress
import dev.ctsetera.ikaranpu.ui.state.SynthesisProgressUiState
import dev.ctsetera.ikaranpu.ui.state.TrackEditorUiState
import dev.ctsetera.ikaranpu.ui.validation.TrackValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TrackEditorStateHolder(
    private val savedStateHandle: SavedStateHandle,
    private val trackValidator: TrackValidator = TrackValidator(),
) {
    companion object {
        private const val KEY_TRACK_NAME = "track_name"
        private const val KEY_CHARACTER_TYPE = "character_type"
        private const val KEY_TEXT_LIST = "text_list"
        private const val KEY_INTERVAL = "interval"
        private const val KEY_PLAY_MODE = "play_mode"
        private const val MAX_TEXT_LIST_SIZE = 10

    }

    private val _uiState = MutableStateFlow(
        TrackEditorUiState(
            trackName = savedStateHandle[KEY_TRACK_NAME] ?: "",
            characterType = savedStateHandle[KEY_CHARACTER_TYPE] ?: CharacterType.ZUNDAMON,
            textList = savedStateHandle[KEY_TEXT_LIST] ?: listOf(""),
            interval = savedStateHandle[KEY_INTERVAL] ?: "",
            playMode = savedStateHandle[KEY_PLAY_MODE] ?: PlayMode.NORMAL,
        )
    )
    val uiState: StateFlow<TrackEditorUiState> = _uiState

    fun changeTrackName(trackName: String) {
        savedStateHandle[KEY_TRACK_NAME] = trackName
        _uiState.update {
            it.copy(
                trackName = trackName,
                validation = it.validation.copy(
                    trackNameError = trackValidator.validateTrackName(trackName),
                ),
            )
        }
    }

    fun changeCharacterType(characterType: CharacterType) {
        savedStateHandle[KEY_CHARACTER_TYPE] = characterType
        _uiState.update { it.copy(characterType = characterType) }
    }

    fun changeText(index: Int, text: String) {
        updateTextList {
            this[index] = text
        }
    }

    fun removeText(index: Int) {
        updateTextList {
            if (size == 1) {
                this[index] = ""
            } else {
                removeAt(index)
            }
        }
    }

    fun addText() {
        updateTextList {
            if (size < MAX_TEXT_LIST_SIZE) add("")
        }
    }

    fun changeInterval(interval: String) {
        savedStateHandle[KEY_INTERVAL] = interval
        _uiState.update {
            it.copy(
                interval = interval,
                validation = it.validation.copy(
                    intervalError = trackValidator.validateInterval(interval),
                ),
            )
        }
    }

    fun changePlayMode(playMode: PlayMode) {
        savedStateHandle[KEY_PLAY_MODE] = playMode
        _uiState.update { it.copy(playMode = playMode) }
    }

    fun validate(required: Boolean): Boolean {
        val state = _uiState.value
        val validation = trackValidator.validate(
            trackName = state.trackName,
            textList = state.textList,
            interval = state.interval,
            required = required,
        )
        _uiState.update { it.copy(validation = validation) }
        return validation.isValid
    }

    fun setSaving(isSaving: Boolean) {
        _uiState.update {
            it.copy(
                isSaving = isSaving,
                isSaveCompleted = if (isSaving) false else it.isSaveCompleted,
            )
        }
    }

    fun markSaveCompleted() {
        _uiState.update {
            it.copy(
                isSaveCompleted = true,
                synthesisProgress = null,
            )
        }
    }

    fun updateProgress(progress: TrackProgress) {
        val synthesisProgress = when (progress) {
            is TrackProgress.Downloading ->
                SynthesisProgressUiState(progress.current, progress.total)

            is TrackProgress.Downloaded ->
                if (progress.current == progress.total) null else _uiState.value.synthesisProgress

            else -> null
        }
        _uiState.update { it.copy(synthesisProgress = synthesisProgress) }
    }

    fun cancelSaving() {
        _uiState.update {
            it.copy(
                isSaving = false,
                isSaveCompleted = false,
                synthesisProgress = null,
            )
        }
    }

    fun initializeFrom(track: Track) {
        _uiState.update {
            it.copy(
                trackName = if (savedStateHandle.contains(KEY_TRACK_NAME)) {
                    it.trackName
                } else {
                    track.trackName
                },
                characterType = if (savedStateHandle.contains(KEY_CHARACTER_TYPE)) {
                    it.characterType
                } else {
                    track.characterType
                },
                textList = if (savedStateHandle.contains(KEY_TEXT_LIST)) {
                    it.textList
                } else {
                    track.textList.ifEmpty { listOf("") }
                },
                interval = if (savedStateHandle.contains(KEY_INTERVAL)) {
                    it.interval
                } else {
                    track.interval.toString()
                },
                playMode = if (savedStateHandle.contains(KEY_PLAY_MODE)) {
                    it.playMode
                } else {
                    track.playMode
                },
            )
        }
    }

    private fun updateTextList(update: MutableList<String>.() -> Unit) {
        val textList = _uiState.value.textList.toMutableList().apply(update).toList()
        savedStateHandle[KEY_TEXT_LIST] = textList
        _uiState.update {
            it.copy(
                textList = textList,
                validation = it.validation.copy(
                    textListErrors = trackValidator.validateTextList(textList),
                ),
            )
        }
    }
}
