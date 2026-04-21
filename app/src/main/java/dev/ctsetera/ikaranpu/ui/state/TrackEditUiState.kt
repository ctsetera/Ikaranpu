package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.UiText
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode

data class TrackEditUiState(
    val isInProgress: Boolean = false,
    val isDownloadSuccess: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val trackName: String,
    val characterType: CharacterType,
    val textList: List<String> = listOf(""),
    val interval: String,
    val playMode: PlayMode,
    val startText: String,
    val endText: String,
    val validateTrackName: UiText? = null,
    val validateTextList: List<UiText?> = listOf(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    ),
    val validateInterval: UiText? = null,
    val errorMessageId: Int? = null,
)