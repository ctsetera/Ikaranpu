package dev.ctsetera.ikaranpu

import dev.ctsetera.ikaranpu.domain.model.TrackError

fun TrackError.getMessageId(): Int {
    return when (this) {
        is TrackError.NotFound -> R.string.error_track_not_found
        is TrackError.DatabaseFailure -> R.string.error_database
    }
}