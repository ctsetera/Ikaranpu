package dev.ctsetera.ikaranpu.domain.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.TrackProgress
import dev.ctsetera.ikaranpu.domain.repository.IVoiceRepository

class TrackVoiceSynthesisService(
    private val voiceRepository: IVoiceRepository,
    private val progressSink: suspend (TrackProgress) -> Unit,
) {
    suspend fun synthesize(
        textList: List<String>,
        characterType: CharacterType,
    ): Result<List<ByteArray>, Error> {
        val filteredList = textList.filter { it.isNotEmpty() }
        val voiceList = mutableListOf<ByteArray>()

        filteredList.forEachIndexed { index, text ->
            progressSink(
                TrackProgress.Downloading(
                    current = index + 1,
                    total = filteredList.size,
                )
            )

            when (
                val result = voiceRepository.generateAndDownload(
                    text = text,
                    characterType = characterType,
                )
            ) {
                is Ok -> {
                    voiceList.add(result.value)

                    progressSink(
                        TrackProgress.Downloaded(
                            current = index + 1,
                            total = filteredList.size,
                        )
                    )
                }

                is Err -> {
                    progressSink(
                        TrackProgress.Failed(
                            err = result.error,
                        )
                    )
                    return result
                }
            }
        }

        return Ok(voiceList)
    }
}
