package dev.ctsetera.ikaranpu.domain.model

sealed interface Error {
    data object TrackNotFound : Error
    data object DatabaseFailure : Error
}