@file:Suppress("TestFunctionName", "NonAsciiCharacters")

package dev.ctsetera.ikaranpu.ui.stateholder

import androidx.lifecycle.SavedStateHandle
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackProgress
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.ui.util.UiText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackEditorStateHolderTest {
    @Test
    fun 入力を変更すると状態とSavedStateHandleが更新される() {
        val savedStateHandle = SavedStateHandle()
        val holder = TrackEditorStateHolder(savedStateHandle)

        holder.changeTrackName("新しいトラック")
        holder.changeCharacterType(CharacterType.METAN)
        holder.changeInterval("20")
        holder.changePlayMode(PlayMode.RANDOM)

        val state = holder.uiState.value
        assertEquals("新しいトラック", state.trackName)
        assertEquals(CharacterType.METAN, state.characterType)
        assertEquals("20", state.interval)
        assertEquals(PlayMode.RANDOM, state.playMode)
        assertEquals("新しいトラック", savedStateHandle["track_name"])
        assertEquals(CharacterType.METAN, savedStateHandle["character_type"])
        assertEquals("20", savedStateHandle["interval"])
        assertEquals(PlayMode.RANDOM, savedStateHandle["play_mode"])
    }

    @Test
    fun 最後のテキストを削除すると項目を残して空文字にする() {
        val holder = TrackEditorStateHolder(
            SavedStateHandle(mapOf("text_list" to listOf("text")))
        )

        holder.removeText(0)

        assertEquals(listOf(""), holder.uiState.value.textList)
    }

    @Test
    fun テキストは最大件数を超えて追加されない() {
        val texts = List(10) { "text$it" }
        val holder = TrackEditorStateHolder(
            SavedStateHandle(mapOf("text_list" to texts))
        )

        holder.addText()

        assertEquals(texts, holder.uiState.value.textList)
    }

    @Test
    fun 通常保存の検証に失敗すると検証結果が状態へ反映される() {
        val holder = TrackEditorStateHolder(SavedStateHandle())

        val isValid = holder.validate(required = true)

        assertFalse(isValid)
        assertEquals(
            UiText.StringResource(R.string.validation_track_name_required),
            holder.uiState.value.validation.trackNameError,
        )
    }

    @Test
    fun 空の下書き保存は検証に成功する() {
        val holder = TrackEditorStateHolder(SavedStateHandle())

        val isValid = holder.validate(required = false)

        assertTrue(isValid)
    }

    @Test
    fun DBのトラックから初期状態を設定する() {
        val holder = TrackEditorStateHolder(SavedStateHandle())

        holder.initializeFrom(createTrack())

        val state = holder.uiState.value
        assertEquals("DBトラック", state.trackName)
        assertEquals(CharacterType.METAN, state.characterType)
        assertEquals(listOf("DBテキスト"), state.textList)
        assertEquals("30", state.interval)
        assertEquals(PlayMode.RANDOM, state.playMode)
    }

    @Test
    fun 復元済み項目だけを残して他の項目はDBのトラックから設定する() {
        val holder = TrackEditorStateHolder(
            SavedStateHandle(mapOf("track_name" to "編集中の名前"))
        )

        holder.initializeFrom(createTrack())

        val state = holder.uiState.value
        assertEquals("編集中の名前", state.trackName)
        assertEquals(CharacterType.METAN, state.characterType)
        assertEquals(listOf("DBテキスト"), state.textList)
        assertEquals("30", state.interval)
        assertEquals(PlayMode.RANDOM, state.playMode)
    }

    @Test
    fun 音声生成中は進捗を表示して完了時に非表示にする() {
        val holder = TrackEditorStateHolder(SavedStateHandle())

        holder.updateProgress(TrackProgress.Downloading(current = 1, total = 2))
        assertEquals(1, holder.uiState.value.synthesisProgress?.current)
        assertEquals(2, holder.uiState.value.synthesisProgress?.total)

        holder.updateProgress(TrackProgress.Downloaded(current = 2, total = 2))
        assertNull(holder.uiState.value.synthesisProgress)
    }

    @Test
    fun 保存をキャンセルすると保存中状態と進捗を解除する() {
        val holder = TrackEditorStateHolder(SavedStateHandle())
        holder.setSaving(true)
        holder.updateProgress(TrackProgress.Downloading(current = 1, total = 2))

        holder.cancelSaving()

        assertFalse(holder.uiState.value.isSaving)
        assertNull(holder.uiState.value.synthesisProgress)
    }

    private fun createTrack(): Track {
        return Track(
            trackName = "DBトラック",
            characterType = CharacterType.METAN,
            textList = listOf("DBテキスト"),
            voiceList = emptyList(),
            interval = 30,
            playMode = PlayMode.RANDOM,
            state = TrackState.PLAYABLE,
        )
    }
}
