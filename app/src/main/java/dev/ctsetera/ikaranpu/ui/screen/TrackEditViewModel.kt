package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.UiText
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackByTrackIdUseCase
import dev.ctsetera.ikaranpu.domain.usecase.UpdateTrackUseCase
import dev.ctsetera.ikaranpu.getMessageId
import dev.ctsetera.ikaranpu.ui.state.TrackEditUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackEditViewModel(
    private val trackId: Long,
    private val getTrackByTrackIdUseCase: GetTrackByTrackIdUseCase,
    private val updateTrackUseCase: UpdateTrackUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        private const val KEY_TRACK_NAME = "track_name"
        private const val KEY_CHARACTER_TYPE = "character_type"
        private const val KEY_TEXT_LIST = "text_list"
        private const val KEY_INTERVAL = "interval"
        private const val KEY_PLAY_MODE = "play_mode"
    }

    private val _uiState = MutableStateFlow(
        TrackEditUiState(
            trackName = savedStateHandle[KEY_TRACK_NAME] ?: "",
            characterType = savedStateHandle[KEY_CHARACTER_TYPE] ?: CharacterType.ZUNDAMON,
            textList = savedStateHandle[KEY_TEXT_LIST] ?: listOf(""),
            interval = savedStateHandle[KEY_INTERVAL] ?: "",
            playMode = savedStateHandle[KEY_PLAY_MODE] ?: PlayMode.NORMAL,
        )
    )
    val uiState: StateFlow<TrackEditUiState> = _uiState

    init {
        getTrack()
    }

    private fun getTrack() = viewModelScope.launch(Dispatchers.IO) {
        _uiState.update { state ->
            state.copy(isInProgress = true)
        }

        getTrackByTrackIdUseCase(trackId)
            .onSuccess { track ->
                _uiState.update { state ->
                    state.copy(
                        trackName = track.trackName,
                        characterType = track.characterType,
                        textList = track.textList,
                        interval = track.interval.toString(),
                        playMode = track.playMode,
                    )
                }
            }
            .onFailure {
                _uiState.update { state ->
                    state.copy(
                        errorMessageId = it.getMessageId(),
                    )
                }
            }

        _uiState.update { state ->
            state.copy(isInProgress = false)
        }
    }

    fun changeTrackName(trackName: String) {
        // バリデーション
        _uiState.update {
            it.copy(
                validateTrackName = if (trackName.isEmpty()) {
                    UiText.StringResource(R.string.validation_track_name_required)
                } else if (trackName.length > 20) {
                    UiText.StringResource(R.string.validation_track_name_max_20)
                } else {
                    null
                }
            )
        }

        savedStateHandle[KEY_TRACK_NAME] = trackName
        _uiState.update { it.copy(trackName = trackName) }
    }

    fun changeCharacterType(characterType: CharacterType) {
        savedStateHandle[KEY_CHARACTER_TYPE] = characterType
        _uiState.update { it.copy(characterType = characterType) }
    }

    fun changeTextListItem(index: Int, text: String) {
        val oldList = _uiState.value.textList
        val newList = oldList.toMutableList()

        newList[index] = text

        // バリデーション
        val validateTextList = _uiState.value.validateTextList.toMutableList()
        newList.forEachIndexed { i, v ->
            validateTextList[i] =
                if (v.length > 20) {
                    UiText.StringResource(R.string.validation_track_list_item_max_20)
                } else {
                    null
                }
        }
        if (newList.none { it.isNotEmpty() }) {
            validateTextList[0] =
                UiText.StringResource(R.string.validation_track_list_item_required)
        } else {
            validateTextList[0] = null
        }
        _uiState.update { state ->
            state.copy(validateTextList = validateTextList)
        }

        savedStateHandle[KEY_TEXT_LIST] = newList.toList()
        _uiState.update { it.copy(textList = newList.toList()) }
    }

    fun removeTextListItem(index: Int) {
        val oldList = _uiState.value.textList
        val newList = oldList.toMutableList()

        newList.removeAt(index)

        savedStateHandle[KEY_TEXT_LIST] = newList.toList()
        _uiState.update { it.copy(textList = newList.toList()) }
    }

    fun addTextListItem() {
        val oldList = _uiState.value.textList
        val newList = oldList.toMutableList()

        if (newList.size < 10) newList += ""

        savedStateHandle[KEY_TEXT_LIST] = newList.toList()
        _uiState.update { it.copy(textList = newList.toList()) }
    }

    fun changeInterval(interval: String) {
        // バリデーション
        _uiState.update {
            it.copy(
                validateInterval = if (interval.isEmpty()) {
                    UiText.StringResource(R.string.validation_track_interval_required)
                } else if (interval.toIntOrNull() == null) {
                    UiText.StringResource(R.string.validation_track_interval_num)
                } else if (interval.toInt() < 10) {
                    UiText.StringResource(R.string.validation_track_interval_min_10)
                } else if (interval.toInt() > 1000) {
                    UiText.StringResource(R.string.validation_track_interval_max_1000)
                } else {
                    null
                }
            )
        }

        savedStateHandle[KEY_INTERVAL] = interval
        _uiState.update { it.copy(interval = interval) }
    }

    fun changePlayMode(playMode: PlayMode) {
        savedStateHandle[KEY_PLAY_MODE] = playMode
        _uiState.update { it.copy(playMode = playMode) }
    }

    fun updateTrack(
        isActive: Boolean,
    ) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.update { state ->
            state.copy(isInProgress = true)
        }

        if (isActive) {
            if (!validateAll()) {
                _uiState.update { state ->
                    state.copy(isInProgress = false)
                }
                return@launch
            }
        }

        updateTrackUseCase(
            trackId = trackId,
            trackName = _uiState.value.trackName,
            characterType = _uiState.value.characterType,
            textList = _uiState.value.textList,
            interval = _uiState.value.interval.toIntOrNull() ?: 0,
            playMode = _uiState.value.playMode,
            state = if (isActive) TrackState.PLAYABLE else TrackState.DRAFT,
        )
            .onSuccess {
                _uiState.update { state ->
                    state.copy(isSavedSuccess = true)
                }
            }
            .onFailure {
                _uiState.update { state ->
                    state.copy(
                        isSavedSuccess = false,
                        errorMessageId = it.getMessageId(),
                    )
                }
            }

        _uiState.update { state ->
            state.copy(isInProgress = false)
        }
    }

    private fun validateAll(): Boolean {
        val state = _uiState.value

        var hasError = false

        // --- TrackName ---
        val trackNameError =
            when {
                state.trackName.isBlank() ->
                    UiText.StringResource(R.string.validation_track_name_required)

                state.trackName.length > 20 ->
                    UiText.StringResource(R.string.validation_track_name_max_20)

                else -> null
            }

        if (trackNameError != null) hasError = true


        // --- TextList ---
        val textErrors = state.textList.mapIndexed { index, text ->
            when {
                text.length > 20 ->
                    UiText.StringResource(R.string.validation_track_list_item_max_20)

                index == 0 && state.textList.none { it.isNotBlank() } ->
                    UiText.StringResource(R.string.validation_track_list_item_required)

                else -> null
            }
        }.toMutableList()

        if (textErrors.any { it != null }) hasError = true

        // --- Interval ---
        val intervalInt = state.interval.toIntOrNull()

        val intervalError =
            when {
                state.interval.isBlank() ->
                    UiText.StringResource(R.string.validation_track_interval_required)

                intervalInt == null ->
                    UiText.StringResource(R.string.validation_track_interval_num)

                intervalInt < 10 ->
                    UiText.StringResource(R.string.validation_track_interval_min_10)

                intervalInt > 1000 ->
                    UiText.StringResource(R.string.validation_track_interval_max_1000)

                else -> null
            }

        if (intervalError != null) hasError = true


        // UI更新
        _uiState.update {
            it.copy(
                validateTrackName = trackNameError,
                validateTextList = textErrors,
                validateInterval = intervalError,
            )
        }

        return !hasError
    }
}