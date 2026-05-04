package dev.ctsetera.ikaranpu.data.remote

data class VoiceSynthesizeResponse(
    val success: Boolean,
    val audioStatusUrl: String,
    val mp3DownloadUrl: String,
)
