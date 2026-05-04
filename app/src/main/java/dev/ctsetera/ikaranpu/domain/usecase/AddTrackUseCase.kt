package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.domain.repository.ITrackRepository
import dev.ctsetera.ikaranpu.domain.repository.IVoiceRepository

class AddTrackUseCase(
    private val trackRepository: ITrackRepository,
    private val voiceRepository: IVoiceRepository,
) {
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
            // ボイスをダウンロードする処理をテキストリスト分繰り返す
            textList.forEach { text ->
                if (text.isNotEmpty()) {
                    val tmpVoice = voiceRepository.generateAndDownload(
                        text,
                        characterType
                    )
                    // エラーであれば即返す
                    if (tmpVoice is Err<Error>) {
                        return tmpVoice
                    }
                    // ボイス（ByteArray）をリストに追加
                    voiceList.add((tmpVoice as Ok).value)
                }
            }
        }

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
        return trackRepository.addTrack(track)
    }
}