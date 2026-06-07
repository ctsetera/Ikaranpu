package dev.ctsetera.ikaranpu.ui.validation

import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.ui.util.UiText

data class TrackValidationResult(
    val trackNameError: UiText? = null,
    val textListErrors: List<UiText?> = emptyList(),
    val intervalError: UiText? = null,
) {
    val isValid: Boolean
        get() = trackNameError == null &&
            textListErrors.none { it != null } &&
            intervalError == null
}

class TrackValidator {
    companion object {
        const val MIN_INTERVAL = 10
    }

    fun validate(
        trackName: String,
        textList: List<String>,
        interval: String,
        required: Boolean,
    ): TrackValidationResult {
        return TrackValidationResult(
            trackNameError = validateTrackName(trackName, required),
            textListErrors = validateTextList(textList, required),
            intervalError = validateInterval(interval, required),
        )
    }

    fun validateTrackName(
        trackName: String,
        required: Boolean = true,
    ): UiText? {
        return when {
            required && trackName.isBlank() ->
                UiText.StringResource(R.string.validation_track_name_required)

            trackName.length > Track.MAX_TRACK_NAME_LENGTH ->
                UiText.StringResource(R.string.validation_track_name_max_20)

            else -> null
        }
    }

    fun validateTextList(
        textList: List<String>,
        required: Boolean = true,
    ): List<UiText?> {
        if (textList.isEmpty()) {
            return if (required) {
                listOf(UiText.StringResource(R.string.validation_track_list_item_required))
            } else {
                emptyList()
            }
        }

        val hasText = textList.any { it.isNotBlank() }

        return textList.mapIndexed { index, text ->
            when {
                text.length > Track.MAX_TEXT_LENGTH ->
                    UiText.StringResource(R.string.validation_track_list_item_max_20)

                required && index == 0 && !hasText ->
                    UiText.StringResource(R.string.validation_track_list_item_required)

                else -> null
            }
        }
    }

    fun validateInterval(
        interval: String,
        required: Boolean = true,
    ): UiText? {
        val intervalInt = interval.toIntOrNull()

        return when {
            interval.isBlank() ->
                if (required) {
                    UiText.StringResource(R.string.validation_track_interval_required)
                } else {
                    null
                }

            intervalInt == null ->
                UiText.StringResource(R.string.validation_track_interval_num)

            intervalInt < MIN_INTERVAL ->
                UiText.StringResource(R.string.validation_track_interval_min_10)

            intervalInt > Track.MAX_INTERVAL ->
                UiText.StringResource(R.string.validation_track_interval_max_1000)

            else -> null
        }
    }
}
