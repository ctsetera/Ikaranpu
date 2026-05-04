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

class VoiceRepository(private val api: VoiceApiService) : IVoiceRepository {
    companion object {
        private var lastSynthesizeTimeMillis = 0L
    }

    override suspend fun generateAndDownload(
        text: String,
        characterType: CharacterType,
    ): Result<ByteArray, Error> {
        return runCatching {
            // 最後のsynthesize呼び出しから5秒は待つ
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastSynthesizeTimeMillis < 5000L) {
                delay(5000 - (currentTimeMillis - lastSynthesizeTimeMillis))
            }

            // ボイス生成をリクエスト
            val response = api.synthesize(
                text = text,
                speaker = characterType.toApiId(),
            )
            if (!response.success) return Err(Error.ApiServerFailure)

            // synthesize完了時間を記録
            lastSynthesizeTimeMillis = System.currentTimeMillis()

            while (true) {
                // サーバ側でボイスが生成されるのを待つ
                delay(2500)

                // ボイスが生成されたかチェック
                val statusResponse = api.getAudioStatus(
                    url = response.audioStatusUrl,
                )
                if (!statusResponse.success) return Err(Error.ApiServerFailure)

                // 生成されていればチェックを終了
                if (statusResponse.isAudioReady) break

                // サーバ側でボイスが生成されるのを待つ
                delay(2500)
            }

            // ボイスをダウンロード
            val audioResponse = api.downloadAudio(response.mp3DownloadUrl)
            if (audioResponse.isSuccessful) {
                audioResponse.body()?.bytes()
            } else {
                return Err(Error.ApiServerFailure)
            }
        }.fold(
            onSuccess = {
                if (it == null) {
                    Err(Error.VoiceEmpty)
                } else {
                    Ok(it)
                }
            },
            onFailure = {
                Err(Error.ApiServerFailure)
            }
        )
    }
}