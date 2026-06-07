@file:Suppress("TestFunctionName", "NonAsciiCharacters")

package dev.ctsetera.ikaranpu.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import dev.ctsetera.ikaranpu.data.remote.api.VoiceApiService
import dev.ctsetera.ikaranpu.data.remote.api.VoiceSynthesizeResponse
import dev.ctsetera.ikaranpu.data.remote.api.VoiceSynthesizeStatusResponse
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.Error
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

class VoiceRepositoryTest {

    @Test
    fun 生成処理がキャンセルされたらCancellationExceptionを再throwする() = runBlocking {
        val api = FakeVoiceApiService(
            synthesizeDelayMillis = 10000L,
        )
        val repository = createRepository(api)

        val deferred = async {
            repository.generateAndDownload(
                text = "こんにちは",
                characterType = CharacterType.ZUNDAMON,
            )
        }
        api.awaitSynthesizeStarted()

        deferred.cancelAndJoin()

        assertTrue(deferred.isCancelled)
    }

    @Test
    fun 生成ステータスが準備完了にならなければVoiceSynthesisTimeoutを返す() = runBlocking {
        val repository = createRepository(
            api = FakeVoiceApiService(
                statusResponse = VoiceSynthesizeStatusResponse(
                    success = true,
                    isAudioReady = false,
                ),
            ),
            pollingTimeoutMillis = 30L,
            firstPollingDelayMillis = 1L,
            pollingIntervalMillis = 1L,
        )

        val result = repository.generateAndDownload(
            text = "こんにちは",
            characterType = CharacterType.ZUNDAMON,
        )

        assertEquals(Err(Error.VoiceSynthesisTimeout), result)
    }

    @Test
    fun 生成ステータスの取得に失敗したらApiServerFailureを返す() = runBlocking {
        val repository = createRepository(
            api = FakeVoiceApiService(
                statusResponse = VoiceSynthesizeStatusResponse(
                    success = false,
                    isAudioReady = false,
                ),
            ),
        )

        val result = repository.generateAndDownload(
            text = "こんにちは",
            characterType = CharacterType.ZUNDAMON,
        )

        assertEquals(Err(Error.ApiServerFailure), result)
    }

    @Test
    fun 音声生成リクエストが失敗したらApiServerFailureを返す() = runBlocking {
        val repository = createRepository(
            FakeVoiceApiService(
                synthesizeResponse = VoiceSynthesizeResponse(
                    success = false,
                    audioStatusUrl = "status",
                    mp3DownloadUrl = "download",
                ),
            )
        )

        val result = repository.generateAndDownload(
            text = "こんにちは",
            characterType = CharacterType.ZUNDAMON,
        )

        assertEquals(Err(Error.ApiServerFailure), result)
    }

    @Test
    fun 音声ダウンロードのレスポンスボディが空ならVoiceEmptyを返す() = runBlocking {
        val repository = createRepository(
            FakeVoiceApiService(
                downloadResponse = Response.success(null),
            )
        )

        val result = repository.generateAndDownload(
            text = "こんにちは",
            characterType = CharacterType.ZUNDAMON,
        )

        assertEquals(Err(Error.VoiceEmpty), result)
    }

    @Test
    fun 音声生成リクエストはRepositoryインスタンスをまたいで直列化される() = runBlocking {
        val firstApi = FakeVoiceApiService()
        val secondApi = FakeVoiceApiService()
        val firstRepository = createRepository(
            api = firstApi,
            minSynthesizeIntervalMillis = 80L,
        )
        val secondRepository = createRepository(
            api = secondApi,
            minSynthesizeIntervalMillis = 80L,
        )

        val first = async {
            firstRepository.generateAndDownload(
                text = "こんにちは",
                characterType = CharacterType.ZUNDAMON,
            )
        }
        val second = async {
            secondRepository.generateAndDownload(
                text = "こんばんは",
                characterType = CharacterType.METAN,
            )
        }

        assertVoiceOk(first.await())
        assertVoiceOk(second.await())
        val synthesizeStartedAt = listOf(
            firstApi.synthesizeStartedAtMillis.single(),
            secondApi.synthesizeStartedAtMillis.single(),
        ).sorted()

        assertTrue(synthesizeStartedAt[1] - synthesizeStartedAt[0] >= 70L)
    }

    @Test
    fun 音声生成からダウンロードまで成功したら音声データを返す() = runBlocking {
        val repository = createRepository(FakeVoiceApiService())

        val result = repository.generateAndDownload(
            text = "こんにちは",
            characterType = CharacterType.ZUNDAMON,
        )

        if (result !is Ok) {
            fail("Expected Ok, but was $result")
            return@runBlocking
        }
        assertArrayEquals("voice".toByteArray(), result.value)
    }

    private fun createRepository(
        api: VoiceApiService,
        minSynthesizeIntervalMillis: Long = 0L,
        pollingTimeoutMillis: Long = 1000L,
        firstPollingDelayMillis: Long = 1L,
        pollingIntervalMillis: Long = 1L,
    ) = VoiceRepository(
        api = api,
        minSynthesizeIntervalMillis = minSynthesizeIntervalMillis,
        pollingTimeoutMillis = pollingTimeoutMillis,
        firstPollingDelayMillis = firstPollingDelayMillis,
        pollingIntervalMillis = pollingIntervalMillis,
    )

    private fun assertVoiceOk(result: com.github.michaelbull.result.Result<ByteArray, Error>) {
        if (result !is Ok) {
            fail("Expected Ok, but was $result")
            return
        }
        assertArrayEquals("voice".toByteArray(), result.value)
    }

    private class FakeVoiceApiService(
        private val synthesizeDelayMillis: Long = 0L,
        private val synthesizeResponse: VoiceSynthesizeResponse = VoiceSynthesizeResponse(
            success = true,
            audioStatusUrl = "status",
            mp3DownloadUrl = "download",
        ),
        private val statusResponse: VoiceSynthesizeStatusResponse = VoiceSynthesizeStatusResponse(
            success = true,
            isAudioReady = true,
        ),
        private val downloadResponse: Response<ResponseBody> = Response.success(
            "voice".toResponseBody(),
        ),
    ) : VoiceApiService {
        val synthesizeStartedAtMillis = mutableListOf<Long>()

        override suspend fun synthesize(
            text: String,
            speaker: Int,
        ): VoiceSynthesizeResponse {
            synthesizeStartedAtMillis.add(System.currentTimeMillis())
            delay(synthesizeDelayMillis)
            return synthesizeResponse
        }

        override suspend fun downloadAudio(
            url: String,
        ): Response<ResponseBody> = downloadResponse

        override suspend fun getAudioStatus(
            url: String,
        ): VoiceSynthesizeStatusResponse = statusResponse

        suspend fun awaitSynthesizeStarted() {
            while (synthesizeStartedAtMillis.isEmpty()) {
                delay(1L)
            }
        }
    }
}
