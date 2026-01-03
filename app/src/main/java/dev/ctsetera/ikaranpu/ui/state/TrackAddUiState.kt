package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode

data class TrackAddUiState(
    val isDownloadSuccess: Boolean = false,
    val isSuccess: Boolean = false,
    val trackName: String,
    val characterType: CharacterType,
    val textList: List<String>,
    val interval: String,
    val playMode: PlayMode,
    val startText: String,
    val endText: String,
    val errorMessageId: Int? = null,
)