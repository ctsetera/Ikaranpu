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
 * @param state トラックの状態
 * @param isPinned ピン止めされているかどうか
 */
@Stable
data class Track(
    val trackId: Long = 0,
    val trackName: String,
    val characterType: CharacterType,
    val textList: List<String>,
    val voiceList: List<ByteArray>,
    val interval: Int,
    val playMode: PlayMode,
    val state: TrackState,
    val isPinned: Boolean = false,
) {
    companion object {
        const val MAX_TRACK_NAME_LENGTH = 30
        const val MAX_TEXT_LENGTH = 50
    }

    init {
        require(trackName.length <= MAX_TRACK_NAME_LENGTH) {
            "Track name must be at most $MAX_TRACK_NAME_LENGTH characters"
        }
        textList.forEach { text ->
            require(text.length <= MAX_TEXT_LENGTH) {
                "Each textList item must be <= $MAX_TEXT_LENGTH chars"
            }
        }
    }
}