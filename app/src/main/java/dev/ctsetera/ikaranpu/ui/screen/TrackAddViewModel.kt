package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.domain.usecase.AddTrackUseCase
import dev.ctsetera.ikaranpu.getMessageId
import dev.ctsetera.ikaranpu.ui.state.TrackAddUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackAddViewModel(
    private val addTrackUseCase: AddTrackUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val KEY_TRACK_NAME = "track_name"
        private const val KEY_CHARACTER_TYPE = "character_type"
        private const val KEY_TEXT_LIST = "text_list"
        private const val KEY_INTERVAL = "interval"
        private const val KEY_PLAY_MODE = "play_mode"
        private const val KEY_START_TEXT = "start_text"
        private const val KEY_END_TEXT = "end_text"
    }

    private val _uiState = MutableStateFlow(
        TrackAddUiState(
            trackName = savedStateHandle[KEY_TRACK_NAME] ?: "",
            characterType = savedStateHandle[KEY_CHARACTER_TYPE] ?: CharacterType.ZUNDAMON,
            textList = savedStateHandle[KEY_TEXT_LIST] ?: listOf(),
            interval = savedStateHandle[KEY_INTERVAL] ?: "",
            playMode = savedStateHandle[KEY_PLAY_MODE] ?: PlayMode.NORMAL,
            startText = savedStateHandle[KEY_START_TEXT] ?: "",
            endText = savedStateHandle[KEY_END_TEXT] ?: "",
        )
    )
    val uiState: StateFlow<TrackAddUiState> = _uiState

    fun changeTrackName(trackName: String) {
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
        savedStateHandle[KEY_INTERVAL] = interval
        _uiState.update { it.copy(interval = interval) }
    }

    fun changePlayMode(playMode: PlayMode) {
        savedStateHandle[KEY_PLAY_MODE] = playMode
        _uiState.update { it.copy(playMode = playMode) }
    }

    fun changeStartText(startText: String) {
        savedStateHandle[KEY_START_TEXT] = startText
        _uiState.update { it.copy(startText = startText) }
    }

    fun changeEndText(endText: String) {
        savedStateHandle[KEY_END_TEXT] = endText
        _uiState.update { it.copy(endText = endText) }
    }

    fun addTrack(isActive: Boolean) {
        viewModelScope.launch {
            // 音声ファイルをダウンロード

            // Model作成

            val track = Track(
                trackName = _uiState.value.trackName,
                characterType = _uiState.value.characterType,
                textList = _uiState.value.textList,
                voiceList = listOf(), // TODO()
                interval = _uiState.value.interval.toIntOrNull() ?: 0,
                playMode = _uiState.value.playMode,
                startText = _uiState.value.startText,
                startVoice = byteArrayOf(), // TODO()
                endText = _uiState.value.endText,
                endVoice = byteArrayOf(), // TODO()
                state = if (isActive) TrackState.PLAYABLE else TrackState.DRAFT,
            )

            addTrackUseCase(track)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(isSuccess = true)
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(
                            isSuccess = false,
                            errorMessageId = it.getMessageId(),
                        )
                    }
                }
        }
    }
}