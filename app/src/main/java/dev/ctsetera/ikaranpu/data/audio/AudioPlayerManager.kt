package dev.ctsetera.ikaranpu.data.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.Error
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

class AudioPlayerManager(
    private val context: Context,
) : IAudioPlayerManager {

    private var mediaPlayer: MediaPlayer? = null

    override suspend fun play(
        mp3List: List<ByteArray>,
        intervalSec: Int,
        random: Boolean,
        volume: Int,
    ): Result<Unit, Error> {
        stop()

        return try {
            var index = 0

            while (currentCoroutineContext().isActive) {
                val voice = if (random) {
                    mp3List[Random.nextInt(0, mp3List.size)]
                } else {
                    mp3List[index]
                }

                // 順番に再生する場合はインデックスをカウントアップする 最後まで再生し終えたらインデックスを0に戻す
                if (!random) {
                    index++
                    if (index == mp3List.size) {
                        index = 0
                    }
                }

                // 再生を実行する前にByteArrayからmp3ファイルを一時的に作成
                val file = when (val result = createTempMp3(voice)) {
                    is Ok -> result.value
                    is Err -> return Err(result.error)
                }

                val startedAt = System.currentTimeMillis()

                try {
                    playFile(file, volume)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(
                        this::class.java.simpleName,
                        "PLAYBACK FAILED\n" + e.stackTraceToString()
                    )
                    return Err(Error.PlaybackFailed)
                } finally {
                    releaseMediaPlayer()

                    file.delete()
                }

                // 音声再生完了から待つ
                val elapsed =
                    System.currentTimeMillis() - startedAt
                val waitMillis =
                    intervalSec * 1000L - elapsed
                if (waitMillis > 0) {
                    delay(waitMillis)
                }
            }

            Ok(Unit)
        } catch (e: CancellationException) {
            releaseMediaPlayer()
            throw e
        }
    }

    override fun stop(): Result<Unit, Error> {
        return runCatching {
            mediaPlayer?.stop()
            releaseMediaPlayer()
        }.fold(
            onSuccess = {
                Ok(Unit)
            },
            onFailure = {
                Err(Error.Unknown(it))
            }
        )
    }

    private suspend fun playFile(
        file: File,
        volume: Int,
    ) {
        val completed = CompletableDeferred<Unit>()

        withContext(Dispatchers.Main) {
            mediaPlayer = MediaPlayer().apply {
                try {
                    setDataSource(file.absolutePath)

                    setOnCompletionListener {
                        completed.complete(Unit)
                    }

                    setOnErrorListener { _, _, _ ->
                        completed.completeExceptionally(
                            RuntimeException("Playback failed")
                        )
                        true
                    }

                    prepare()

                    setVolume(volume / 100f, volume / 100f)

                    start()
                } catch (e: Exception) {
                    completed.completeExceptionally(e)
                }
            }
        }

        completed.await()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun createTempMp3(
        bytes: ByteArray,
    ): Result<File, Error> {

        return runCatching {

            File.createTempFile(
                "temp_audio",
                ".mp3",
                context.cacheDir
            ).apply {
                writeBytes(bytes)
            }

        }.fold(
            onSuccess = {
                Ok(it)
            },
            onFailure = {
                Err(Error.FileCreateFailed)
            }
        )
    }
}
