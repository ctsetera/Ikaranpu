package dev.ctsetera.ikaranpu.data.audio

import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.Error

interface IAudioPlayerManager {
    suspend fun play(
        mp3List: List<ByteArray>,
        intervalSec: Int = 0,
        random: Boolean,
        volume: Int = 50,
    ): Result<Unit, Error>

    fun stop(): Result<Unit, Error>
}