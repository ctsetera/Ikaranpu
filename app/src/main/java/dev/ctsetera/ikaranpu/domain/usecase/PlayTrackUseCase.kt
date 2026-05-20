package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import dev.ctsetera.ikaranpu.data.audio.IAudioPlayerManager
import dev.ctsetera.ikaranpu.data.repository.SettingsRepository
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.repository.ITrackRepository
import kotlinx.coroutines.flow.first

class PlayTrackUseCase(
    private val settingsRepository: SettingsRepository,
    private val trackRepository: ITrackRepository,
    private val audioPlayerManager: IAudioPlayerManager,
) {
    suspend operator fun invoke(
        trackId: Long,
    ): Result<Unit, Error> {
        // アプリ側の音量設定を取得
        val volume =
            settingsRepository
                .getSettings()
                .first()
                .volume

        // 音声トラックを取得して再生
        return trackRepository
            .getTrack(trackId)
            .andThen { track ->
                if (track.voiceList.isEmpty()) {
                    return@andThen Err(Error.TrackNotFound)
                }

                audioPlayerManager.play(
                    mp3List = track.voiceList,
                    intervalSec = track.interval,
                    random = track.playMode == PlayMode.RANDOM,
                    volume = volume,
                )
            }
    }

    fun stop(): Result<Unit, Error> {
        // 音声トラックの再生を停止
        return audioPlayerManager.stop()
    }
}