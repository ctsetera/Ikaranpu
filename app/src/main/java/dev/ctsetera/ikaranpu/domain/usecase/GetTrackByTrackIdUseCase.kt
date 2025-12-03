package dev.ctsetera.ikaranpu.domain.usecase

import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackState

class GetTrackByTrackIdUseCase {
    operator fun invoke(trackId: Int): Track {
        return Track(
            1,
            "イカランプ feat. ずんだもん",
            CharacterType.ZUNDAMON,
            listOf("イカランプみて", "イカランプ確認", "イカランプをみるのだ"),
            10,
            PlayMode.NORMAL,
            null,
            null,
            state = TrackState.PLAYABLE,
        )
    }
}