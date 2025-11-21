package dev.ctsetera.ikaranpu.domain.model

import androidx.compose.runtime.Stable

/**
 * トラック
 *
 * @param trackId トラックID
 * @param trackName トラック名
 * @param characterType 読み上げのキャラクタータイプ
 * @param textList 読み上げるテキストのリスト
 * @param interval 読み上げのインターバル（秒）
 * @param playMode プレイモード
 * @param startText 再生開始時に読み上げるテキスト
 * @param endText 再生終了時に読み上げるテキスト
 * @param state トラックの状態
 */
@Stable
data class Track(
    val trackId: Int,
    val trackName: String,
    val characterType: CharacterType,
    val textList: List<String>,
    val interval: Int,
    val playMode: PlayMode,
    val startText: String?,
    val endText: String?,
    val state: TrackState,
) {
    companion object {
        const val MAX_TRACK_NAME_LENGTH = 30
        const val MAX_TEXT_LENGTH = 50
    }

    init {
        require(trackName.isNotBlank()) { "Track name must not be empty" }
        require(trackName.length <= MAX_TRACK_NAME_LENGTH) {
            "Track name must be at most $MAX_TRACK_NAME_LENGTH characters"
        }
        textList.forEach { text ->
            require(text.length <= MAX_TEXT_LENGTH) {
                "Each textList item must be <= $MAX_TEXT_LENGTH chars"
            }
        }
        require(textList.isNotEmpty()) { "Text list must not be empty" }
        require(interval > 0) { "Interval must be greater than 0" }
    }
}