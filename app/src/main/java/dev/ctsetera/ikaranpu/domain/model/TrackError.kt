package dev.ctsetera.ikaranpu.domain.model

sealed interface TrackError {
    data object NotFound : TrackError
    data object DatabaseFailure : TrackError
}