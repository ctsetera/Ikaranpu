package dev.ctsetera.ikaranpu.domain.usecase

import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackState

class GetDraftListUseCase {
    operator fun invoke(): List<Track> {
        return listOf(
            Track(
                5,
                "下書き1",
                CharacterType.ZUNDAMON,
                listOf("イカランプみて", "イカランプ確認", "イカランプをみるのだ"),
                10,
                PlayMode.NORMAL,
                null,
                null,
                state = TrackState.DRAFT,
            ),
            Track(
                6,
                "下書き2",
                CharacterType.METAN,
                listOf("イカランプみて", "イカランプ確認", "イカランプをみるのよ"),
                10,
                PlayMode.NORMAL,
                null,
                null,
                state = TrackState.DRAFT,
            ),
            Track(
                7,
                "下書き3",
                CharacterType.ZUNDAMON,
                listOf("マップみて", "マップ確認", "マップをみるのだ"),
                10,
                PlayMode.NORMAL,
                null,
                null,
                state = TrackState.DRAFT,
            ),
            Track(
                8,
                "下書き4",
                CharacterType.METAN,
                listOf("マップみて", "マップ確認", "マップをみるのよ"),
                10,
                PlayMode.NORMAL,
                null,
                null,
                state = TrackState.DRAFT,
            ),
        )
    }
}