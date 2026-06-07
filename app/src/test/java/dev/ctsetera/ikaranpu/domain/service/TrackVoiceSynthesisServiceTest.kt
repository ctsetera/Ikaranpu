@file:Suppress("TestFunctionName", "NonAsciiCharacters")

package dev.ctsetera.ikaranpu.domain.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.TrackProgress
import dev.ctsetera.ikaranpu.domain.repository.IVoiceRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class TrackVoiceSynthesisServiceTest {
    @Test
    fun 空文字を除外して音声を生成する() = runBlocking {
        val voiceRepository = FakeVoiceRepository()
        val service = TrackVoiceSynthesisService(
            voiceRepository = voiceRepository,
            progressSink = {},
        )

        val result = service.synthesize(
            textList = listOf("text1", "", "text2"),
            characterType = CharacterType.ZUNDAMON,
        )

        if (result is Ok) {
            assertEquals(2, result.value.size)
            assertArrayEquals("text1".toByteArray(), result.value[0])
            assertArrayEquals("text2".toByteArray(), result.value[1])
        } else {
            assertEquals(Ok(emptyList<ByteArray>()), result)
        }
        assertEquals(listOf("text1", "text2"), voiceRepository.requestedTexts)
    }

    @Test
    fun 音声生成に成功すると進捗を通知する() = runBlocking {
        val progressList = mutableListOf<TrackProgress>()
        val service = TrackVoiceSynthesisService(
            voiceRepository = FakeVoiceRepository(),
            progressSink = { progressList.add(it) },
        )

        service.synthesize(
            textList = listOf("text1", "text2"),
            characterType = CharacterType.ZUNDAMON,
        )

        assertEquals(
            listOf(
                TrackProgress.Downloading(current = 1, total = 2),
                TrackProgress.Downloaded(current = 1, total = 2),
                TrackProgress.Downloading(current = 2, total = 2),
                TrackProgress.Downloaded(current = 2, total = 2),
            ),
            progressList,
        )
    }

    @Test
    fun 音声生成に失敗するとFailedを通知してエラーを返す() = runBlocking {
        val progressList = mutableListOf<TrackProgress>()
        val service = TrackVoiceSynthesisService(
            voiceRepository = FakeVoiceRepository(
                resultByText = mapOf(
                    "text2" to Err(Error.ApiServerFailure),
                )
            ),
            progressSink = { progressList.add(it) },
        )

        val result = service.synthesize(
            textList = listOf("text1", "text2", "text3"),
            characterType = CharacterType.ZUNDAMON,
        )

        assertEquals(Err(Error.ApiServerFailure), result)
        assertEquals(
            listOf(
                TrackProgress.Downloading(current = 1, total = 3),
                TrackProgress.Downloaded(current = 1, total = 3),
                TrackProgress.Downloading(current = 2, total = 3),
                TrackProgress.Failed(Error.ApiServerFailure),
            ),
            progressList,
        )
    }

    private class FakeVoiceRepository(
        private val resultByText: Map<String, Result<ByteArray, Error>> = emptyMap(),
    ) : IVoiceRepository {
        val requestedTexts = mutableListOf<String>()

        override suspend fun generateAndDownload(
            text: String,
            characterType: CharacterType,
        ): Result<ByteArray, Error> {
            requestedTexts.add(text)
            return resultByText[text] ?: Ok(text.toByteArray())
        }
    }
}
