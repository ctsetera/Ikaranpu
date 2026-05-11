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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AddTrackUseCase(
    private val trackRepository: ITrackRepository,
    private val voiceRepository: IVoiceRepository,
) {
    private val _progressFlow = MutableSharedFlow<TrackProgress>()
    val progressFlow = _progressFlow.asSharedFlow()

    suspend operator fun invoke(
        trackName: String,
        characterType: CharacterType,
        textList: List<String>,
        interval: Int,
        playMode: PlayMode,
        state: TrackState,
    ): Result<Long, Error> {
        val voiceList: ArrayList<ByteArray> = arrayListOf()

        if (state == TrackState.PLAYABLE) {
            val filteredList = textList.filter { it.isNotEmpty() }

            filteredList.forEachIndexed { index, text ->

                // ダウンロード開始通知
                _progressFlow.emit(
                    TrackProgress.Downloading(
                        current = index + 1,
                        total = filteredList.size,
                    )
                )

                val tmpVoice = voiceRepository.generateAndDownload(
                    text,
                    characterType
                )

                // エラー
                if (tmpVoice is Err<Error>) {

                    _progressFlow.emit(
                        TrackProgress.Failed(
                            err = tmpVoice.error
                        )
                    )

                    return tmpVoice
                }

                voiceList.add((tmpVoice as Ok).value)

                // ダウンロード完了通知
                _progressFlow.emit(
                    TrackProgress.Downloaded(
                        current = index + 1,
                        total = filteredList.size,
                    )
                )
            }
        }

        // 「ローカルに保存」の開始を通知
        _progressFlow.emit(
            TrackProgress.Saving
        )

        // Track組み立て
        val track = Track(
            trackName = trackName,
            characterType = characterType,
            textList = textList,
            voiceList = voiceList.toList(),
            interval = interval,
            playMode = playMode,
            state = state,
        )

        // 保存
        val result = trackRepository.addTrack(track)

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