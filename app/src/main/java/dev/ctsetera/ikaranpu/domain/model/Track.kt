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
    val trackId: Long = 0,
    val trackName: String,
    val characterType: CharacterType,
    val textList: List<String>,
    val voiceList: List<ByteArray>,
    val interval: Int,
    val playMode: PlayMode,
    val startText: String,
    val startVoice: ByteArray,
    val endText: String,
    val endVoice: ByteArray,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Track

        if (trackId != other.trackId) return false
        if (trackName != other.trackName) return false
        if (characterType != other.characterType) return false
        if (textList != other.textList) return false
        if (voiceList != other.voiceList) return false
        if (interval != other.interval) return false
        if (playMode != other.playMode) return false
        if (startText != other.startText) return false
        if (!startVoice.contentEquals(other.startVoice)) return false
        if (endText != other.endText) return false
        if (!endVoice.contentEquals(other.endVoice)) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trackId.hashCode()
        result = 31 * result + trackName.hashCode()
        result = 31 * result + characterType.hashCode()
        result = 31 * result + textList.hashCode()
        result = 31 * result + voiceList.hashCode()
        result = 31 * result + interval
        result = 31 * result + playMode.hashCode()
        result = 31 * result + startText.hashCode()
        result = 31 * result + startVoice.contentHashCode()
        result = 31 * result + endText.hashCode()
        result = 31 * result + endVoice.contentHashCode()
        result = 31 * result + state.hashCode()
        return result
    }
}