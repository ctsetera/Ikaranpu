@file:Suppress("TestFunctionName", "NonAsciiCharacters")

package dev.ctsetera.ikaranpu.domain.model

import org.junit.Assert.assertTrue
import org.junit.Test

class SemanticVersionTest {

    @Test
    fun vプレフィックス付きのバージョンをパースして比較できる() {
        val current = SemanticVersion.parse("1.0.0")
        val latest = SemanticVersion.parse("v1.1.0")

        assertTrue(latest!! > current!!)
    }

    @Test
    fun 同じバージョンではプレリリース版より正式版の方が新しい() {
        val preRelease = SemanticVersion.parse("v1.1.0-beta.1")
        val stable = SemanticVersion.parse("v1.1.0")

        assertTrue(preRelease!! < stable!!)
    }
}
