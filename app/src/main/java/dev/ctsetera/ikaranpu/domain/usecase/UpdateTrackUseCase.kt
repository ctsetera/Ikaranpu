package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackProgress
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.domain.repository.ITrackRepository
import dev.ctsetera.ikaranpu.domain.repository.IVoiceRepository
import dev.ctsetera.ikaranpu.domain.service.TrackVoiceSynthesisService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class UpdateTrackUseCase(
    private val trackRepository: ITrackRepository,
    private val voiceRepository: IVoiceRepository,
) {
    private val _progressFlow = MutableSharedFlow<TrackProgress>()
    val progressFlow = _progressFlow.asSharedFlow()

    suspend operator fun invoke(
        trackId: Long,
        trackName: String,
        characterType: CharacterType,
        textList: List<String>,
        interval: Int,
        playMode: PlayMode,
        state: TrackState,
    ): Result<Unit, Error> {
        val voiceList = if (state == TrackState.PLAYABLE) {
            when (
                val result = TrackVoiceSynthesisService(
                    voiceRepository = voiceRepository,
                    progressSink = _progressFlow::emit,
                ).synthesize(
                    textList = textList,
                    characterType = characterType,
                )
            ) {
                is Ok -> result.value
                is Err -> return result
            }
        } else {
            emptyList()
        }

        // 「ローカルに保存」の開始を通知
        _progressFlow.emit(
            TrackProgress.Saving
        )

        // Track組み立て
        val track = Track(
            trackId = trackId,
            trackName = trackName,
            characterType = characterType,
            textList = textList,
            voiceList = voiceList,
            interval = interval,
            playMode = playMode,
            state = state,
        )

        // ④保存
        val result = trackRepository.updateTrack(track)

        // 成功したら「Completed」を通知
        if (result is Ok) {
            _progressFlow.emit(
                TrackProgress.Completed
            )
        }

        // 失敗したら「Error」を通知
        if (result is Err) {
            _progressFlow.emit(
                TrackProgress.Failed(result.error)
            )
        }

        return result
    }
}
