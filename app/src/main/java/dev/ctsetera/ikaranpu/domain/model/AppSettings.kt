package dev.ctsetera.ikaranpu.domain.model

data class AppSettings(
    val volume: Int = 50,
    val checkPreRelease: Boolean = false,
    val updatePostponedAtMillis: Long = 0L,
    val updatePostponedVersion: String = "",
)
