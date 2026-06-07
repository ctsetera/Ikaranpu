package dev.ctsetera.ikaranpu.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.data.remote.api.VoiceApiService
import dev.ctsetera.ikaranpu.data.remote.api.toApiId
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.repository.IVoiceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.cancellation.CancellationException

class VoiceRepository(
    private val api: VoiceApiService,
    private val minSynthesizeIntervalMillis: Long = 5000L,
    private val pollingTimeoutMillis: Long = 60000L,
    private val firstPollingDelayMillis: Long = 100L,
    private val pollingIntervalMillis: Long = 900L,
) : IVoiceRepository {
    companion object {
        private val synthesizeMutex = Mutex()
        private var lastSynthesizeTimeMillis = 0L
    }

    override suspend fun generateAndDownload(
        text: String,
        characterType: CharacterType,
    ): Result<ByteArray, Error> {
        return try {
            val response = synthesizeMutex.withLock {
                // 最後のsynthesize呼び出しから5秒は待つ
                val currentTimeMillis = System.currentTimeMillis()
                val elapsedMillis = currentTimeMillis - lastSynthesizeTimeMillis
                if (elapsedMillis < minSynthesizeIntervalMillis) {
                    delay(minSynthesizeIntervalMillis - elapsedMillis)
                }

                // ボイス生成をリクエスト
                api.synthesize(
                    text = text,
                    speaker = characterType.toApiId(),
                ).also {
                    // synthesize完了時間を記録
                    lastSynthesizeTimeMillis = System.currentTimeMillis()
                }
            }
            if (!response.success) return Err(Error.ApiServerFailure)

            waitUntilAudioReady(response.audioStatusUrl)
                .let { if (it is Err) return it }

            // ボイスをダウンロード
            val audioResponse = api.downloadAudio(response.mp3DownloadUrl)
            if (audioResponse.isSuccessful) {
                audioResponse.body()?.bytes()
            } else {
                return Err(Error.ApiServerFailure)
            }?.let {
                Ok(it)
            } ?: Err(Error.VoiceEmpty)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Err(Error.ApiServerFailure)
        }
    }

    private suspend fun waitUntilAudioReady(
        audioStatusUrl: String,
    ): Result<Unit, Error> {
        val isReady = withTimeoutOrNull(pollingTimeoutMillis) {
            var audioReady = false
            while (!audioReady) {
                // サーバ側でボイスが生成されるのを待つ
                delay(firstPollingDelayMillis)

                // ボイスが生成されたかチェック
                val statusResponse = api.getAudioStatus(
                    url = audioStatusUrl,
                )
                if (!statusResponse.success) return@withTimeoutOrNull false

                // 生成されていればチェックを終了
                audioReady = statusResponse.isAudioReady

                if (!audioReady) {
                    // サーバ側でボイスが生成されるのを待つ
                    delay(pollingIntervalMillis)
                }
            }
            true
        } ?: false

        return if (isReady) {
            Ok(Unit)
        } else {
            Err(Error.ApiServerFailure)
        }
    }
}
