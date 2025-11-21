package dev.ctsetera.ikaranpu.domain.usecase

import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track

class GetTrackListUseCase {
    operator fun invoke(): List<Track> {
        return listOf(
            Track(
                1,
                "イカランプ",
                CharacterType.ZUNDAMON,
                listOf("イカランプみて", "イカランプ確認", "イカランプをみるのだ"),
                10,
                PlayMode.NORMAL,
                null,
                null,
            ),
            Track(
                2,
                "イカランプ",
                CharacterType.METAN,
                listOf("イカランプみて", "イカランプ確認", "イカランプをみるのよ"),
                10,
                PlayMode.NORMAL,
                null,
                null,
            )
        )
    }
}