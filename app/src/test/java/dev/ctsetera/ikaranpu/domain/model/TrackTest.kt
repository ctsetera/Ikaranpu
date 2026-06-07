@file:Suppress("TestFunctionName", "NonAsciiCharacters")

package dev.ctsetera.ikaranpu.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TrackTest {
    @Test
    fun インターバルが最大値以内ならトラックを生成できる() {
        val track = createTrack(interval = Track.MAX_INTERVAL)

        assertEquals(Track.MAX_INTERVAL, track.interval)
    }

    @Test(expected = IllegalArgumentException::class)
    fun インターバルが最大値を超えるとトラックを生成できない() {
        createTrack(interval = Track.MAX_INTERVAL + 1)
    }

    private fun createTrack(interval: Int): Track {
        return Track(
            trackName = "track",
            characterType = CharacterType.ZUNDAMON,
            textList = listOf("text"),
            voiceList = emptyList(),
            interval = interval,
            playMode = PlayMode.NORMAL,
            state = TrackState.PLAYABLE,
        )
    }
}
