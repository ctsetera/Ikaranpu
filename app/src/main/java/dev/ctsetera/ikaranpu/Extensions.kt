package dev.ctsetera.ikaranpu

import dev.ctsetera.ikaranpu.domain.model.Error

fun Error.getMessageId(): Int {
    return when (this) {
        is Error.TrackNotFound -> R.string.error_track_not_found
        is Error.DatabaseFailure -> R.string.error_database
    }
}