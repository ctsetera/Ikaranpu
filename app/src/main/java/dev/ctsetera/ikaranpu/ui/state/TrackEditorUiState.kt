package dev.ctsetera.ikaranpu.ui.state

import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.ui.validation.TrackValidationResult

data class TrackEditorUiState(
    val isSaving: Boolean = false,
    val trackName: String = "",
    val characterType: CharacterType = CharacterType.ZUNDAMON,
    val textList: List<String> = listOf(""),
    val interval: String = "",
    val playMode: PlayMode = PlayMode.NORMAL,
    val validation: TrackValidationResult = TrackValidationResult(),
    val synthesisProgress: SynthesisProgressUiState? = null,
)

data class SynthesisProgressUiState(
    val current: Int,
    val total: Int,
)
