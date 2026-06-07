@file:Suppress("TestFunctionName", "NonAsciiCharacters")

package dev.ctsetera.ikaranpu.ui.validation

import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.ui.util.UiText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackValidatorTest {
    private val validator = TrackValidator()

    @Test
    fun 有効な通常トラックは検証に成功する() {
        val result = validator.validate(
            trackName = "track",
            textList = listOf("text"),
            interval = TrackValidator.MIN_INTERVAL.toString(),
            required = true,
        )

        assertTrue(result.isValid)
        assertNull(result.trackNameError)
        assertEquals(listOf(null), result.textListErrors)
        assertNull(result.intervalError)
    }

    @Test
    fun 空の下書きは検証に成功する() {
        val result = validator.validate(
            trackName = "",
            textList = listOf(""),
            interval = "",
            required = false,
        )

        assertTrue(result.isValid)
    }

    @Test
    fun 空の通常トラックは必須エラーになる() {
        val result = validator.validate(
            trackName = " ",
            textList = listOf(" ", ""),
            interval = " ",
            required = true,
        )

        assertFalse(result.isValid)
        assertEquals(
            stringResource(R.string.validation_track_name_required),
            result.trackNameError,
        )
        assertEquals(
            listOf(
                stringResource(R.string.validation_track_list_item_required),
                null,
            ),
            result.textListErrors,
        )
        assertEquals(
            stringResource(R.string.validation_track_interval_required),
            result.intervalError,
        )
    }

    @Test
    fun トラック名が最大文字数以内ならエラーにならない() {
        val error = validator.validateTrackName(
            trackName = "a".repeat(Track.MAX_TRACK_NAME_LENGTH),
        )

        assertNull(error)
    }

    @Test
    fun トラック名が最大文字数を超えるとエラーになる() {
        val error = validator.validateTrackName(
            trackName = "a".repeat(Track.MAX_TRACK_NAME_LENGTH + 1),
        )

        assertEquals(stringResource(R.string.validation_track_name_max_20), error)
    }

    @Test
    fun 必須のテキストリストが空ならエラーになる() {
        val errors = validator.validateTextList(emptyList(), required = true)

        assertEquals(
            listOf(stringResource(R.string.validation_track_list_item_required)),
            errors,
        )
    }

    @Test
    fun 最大文字数を超えたテキストだけエラーになる() {
        val errors = validator.validateTextList(
            listOf(
                "a".repeat(Track.MAX_TEXT_LENGTH),
                "b".repeat(Track.MAX_TEXT_LENGTH + 1),
            ),
        )

        assertEquals(
            listOf(null, stringResource(R.string.validation_track_list_item_max_20)),
            errors,
        )
    }

    @Test
    fun インターバルが整数でなければエラーになる() {
        val error = validator.validateInterval("10.5")

        assertEquals(stringResource(R.string.validation_track_interval_num), error)
    }

    @Test
    fun インターバルが最小値未満ならエラーになる() {
        val error = validator.validateInterval((TrackValidator.MIN_INTERVAL - 1).toString())

        assertEquals(stringResource(R.string.validation_track_interval_min_10), error)
    }

    @Test
    fun インターバルが境界値ならエラーにならない() {
        assertNull(validator.validateInterval(TrackValidator.MIN_INTERVAL.toString()))
        assertNull(validator.validateInterval(Track.MAX_INTERVAL.toString()))
    }

    @Test
    fun インターバルが最大値を超えるとエラーになる() {
        val error = validator.validateInterval((Track.MAX_INTERVAL + 1).toString())

        assertEquals(stringResource(R.string.validation_track_interval_max_1000), error)
    }

    private fun stringResource(resId: Int) = UiText.StringResource(resId)
}
