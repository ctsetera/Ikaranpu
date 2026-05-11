package dev.ctsetera.ikaranpu.domain.model

sealed class TrackProgress {
    data class Downloading(
        val current: Int,
        val total: Int,
    ) : TrackProgress()

    data class Downloaded(
        val current: Int,
        val total: Int,
    ) : TrackProgress()

    data object Saving : TrackProgress()

    data object Completed : TrackProgress()

    data class Failed(val err: Error) : TrackProgress()
}