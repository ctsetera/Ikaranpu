package dev.ctsetera.ikaranpu.data.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import dev.ctsetera.ikaranpu.domain.model.Error
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

class AudioPlayerManager(
    private val context: Context,
) : IAudioPlayerManager {

    private var mediaPlayer: MediaPlayer? = null
    private var loopJob: Job? = null

    override suspend fun play(
        mp3List: List<ByteArray>,
        intervalSec: Int,
        random: Boolean,
        volume: Int,
    ): Result<Unit, Error> {
        stop()

        return binding {

            loopJob = CoroutineScope(Dispatchers.IO).launch {

                var index = 0

                while (isActive) {
                    // 再生を実行する前にByteArrayからmp3ファイルを一時的に作成
                    val file = if (random) {
                        createTempMp3(mp3List[Random.nextInt(0, mp3List.size)])
                    } else {
                        createTempMp3(mp3List[index])
                    }.mapError { it }.bind()

                    // 順番に再生する場合はインデックスをカウントアップする 最後まで再生し終えたらインデックスを0に戻す
                    if (!random) {
                        index++
                        if (index == mp3List.size) {
                            index = 0
                        }
                    }

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

                    val startedAt = System.currentTimeMillis()

                    try {
                        // 再生
                        completed.await()
                    } catch (e: CancellationException) {
                        // stop() 時は正常終了
                        break
                    } catch (e: Exception) {
                        Log.e(
                            this::class.java.simpleName,
                            "PLAYBACK FAILED\n" + e.stackTraceToString()
                        )
                        Err(Error.PlaybackFailed)
                            .bind<Unit>()
                    } finally {
                        mediaPlayer?.release()
                        mediaPlayer = null

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
            }
        }
    }

    override fun stop(): Result<Unit, Error> {
        return runCatching {
            loopJob?.cancel()
            loopJob = null

            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

        }.fold(
            onSuccess = {
                Ok(Unit)
            },
            onFailure = {
                Err(Error.Unknown(it))
            }
        )
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