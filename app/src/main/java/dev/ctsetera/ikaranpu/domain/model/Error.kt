package dev.ctsetera.ikaranpu.domain.model

sealed class Error {
    data object TrackNotFound : Error()
    data object VoiceEmpty : Error()
    data object DatabaseFailure : Error()
    data object ApiServerFailure : Error()
    data object FileCreateFailed : Error()
    data object PlaybackFailed : Error()
    data class Unknown(
        val throwable: Throwable,
    ) : Error()
}