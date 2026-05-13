package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.UiText
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode

data class TrackEditUiState(
    val isSaving: Boolean = false,
    val trackName: String = "",
    val characterType: CharacterType = CharacterType.ZUNDAMON,
    val textList: List<String> = listOf(""),
    val interval: String = "",
    val playMode: PlayMode = PlayMode.NORMAL,
    val startText: String = "",
    val endText: String = "",
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
    val dialogSowing: Boolean = false,
    val dialogProgressCurrent: Int = 0,
    val dialogProgressTotal: Int = 10,
)