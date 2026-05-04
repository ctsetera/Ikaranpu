package dev.ctsetera.ikaranpu.data.remote

import dev.ctsetera.ikaranpu.domain.model.CharacterType

fun CharacterType.toApiId(): Int = when (this) {
    CharacterType.ZUNDAMON -> 3
    CharacterType.METAN -> 2
}