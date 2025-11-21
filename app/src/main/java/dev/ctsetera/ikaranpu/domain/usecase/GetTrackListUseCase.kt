package dev.ctsetera.ikaranpu.domain.usecase

import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track

class GetTrackListUseCase {
    operator fun invoke(): List<Track> {
        return listOf(
            Track(
                1,
                "イカランプ feat. ずんだもん",
                CharacterType.ZUNDAMON,
                listOf("イカランプみて", "イカランプ確認", "イカランプをみるのだ"),
                10,
                PlayMode.NORMAL,
                null,
                null,
            ),
            Track(
                2,
                "イカランプ feat. 四国めたん",
                CharacterType.METAN,
                listOf("イカランプみて", "イカランプ確認", "イカランプをみるのよ"),
                10,
                PlayMode.NORMAL,
                null,
                null,
            ),
            Track(
                3,
                "マップ確認 feat. ずんだもん",
                CharacterType.ZUNDAMON,
                listOf("マップみて", "マップ確認", "マップをみるのだ"),
                10,
                PlayMode.NORMAL,
                null,
                null,
            ),
            Track(
                4,
                "マップ確認 feat. 四国めたん",
                CharacterType.METAN,
                listOf("マップみて", "マップ確認", "マップをみるのよ"),
                10,
                PlayMode.NORMAL,
                null,
                null,
            ),
        )
    }
}