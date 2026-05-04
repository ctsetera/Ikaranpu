package dev.ctsetera.ikaranpu.data.remote.api

data class VoiceSynthesizeResponse(
    val success: Boolean,
    val audioStatusUrl: String,
    val mp3DownloadUrl: String,
)
