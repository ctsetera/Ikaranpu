package dev.ctsetera.ikaranpu

import dev.ctsetera.ikaranpu.domain.model.Error

fun Error.getMessageId(): Int {
    return when (this) {
        is Error.TrackNotFound -> R.string.error_track_not_found
        is Error.DatabaseFailure -> R.string.error_database
        is Error.ApiServerFailure -> R.string.error_connection_failed
        is Error.VoiceEmpty -> R.string.error_voice_empty
    }
}