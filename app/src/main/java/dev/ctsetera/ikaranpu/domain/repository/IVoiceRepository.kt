package dev.ctsetera.ikaranpu.domain.repository

import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.Error

interface IVoiceRepository {
    suspend fun generateAndDownload(
        text: String,
        characterType: CharacterType,
    ): Result<ByteArray, Error>
}