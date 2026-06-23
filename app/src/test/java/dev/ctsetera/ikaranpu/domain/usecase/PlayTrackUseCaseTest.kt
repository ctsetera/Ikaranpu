@file:Suppress("TestFunctionName", "NonAsciiCharacters")

package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.data.audio.IAudioPlayerManager
import dev.ctsetera.ikaranpu.domain.model.AppSettings
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.domain.repository.ISettingsRepository
import dev.ctsetera.ikaranpu.domain.repository.ITrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlayTrackUseCaseTest {
    @Test
    fun 設定の音量とトラック情報をAudioPlayerManagerに渡して再生する() = runBlocking {
        val voice = "voice".toByteArray()
        val track = createTrack(
            voiceList = listOf(voice),
            interval = 15,
            playMode = PlayMode.RANDOM,
        )
        val audioPlayerManager = FakeAudioPlayerManager()
        val useCase = PlayTrackUseCase(
            settingsRepository = FakeSettingsRepository(volume = 80),
            trackRepository = FakeTrackRepository(trackResult = Ok(track)),
            audioPlayerManager = audioPlayerManager,
        )

        val result = useCase(trackId = 10L)

        assertEquals(Ok(Unit), result)
        assertArrayEquals(voice, audioPlayerManager.playedMp3List?.single())
        assertEquals(15, audioPlayerManager.playedIntervalSec)
        assertEquals(true, audioPlayerManager.playedRandom)
        assertEquals(80, audioPlayerManager.playedVolume)
    }

    @Test
    fun 音声リストが空ならTrackNotFoundを返して再生しない() = runBlocking {
        val audioPlayerManager = FakeAudioPlayerManager()
        val useCase = PlayTrackUseCase(
            settingsRepository = FakeSettingsRepository(volume = 80),
            trackRepository = FakeTrackRepository(
                trackResult = Ok(createTrack(voiceList = emptyList())),
            ),
            audioPlayerManager = audioPlayerManager,
        )

        val result = useCase(trackId = 10L)

        assertEquals(Err(Error.TrackNotFound), result)
        assertNull(audioPlayerManager.playedMp3List)
    }

    @Test
    fun トラック取得に失敗したらAudioPlayerManagerで再生しない() = runBlocking {
        val audioPlayerManager = FakeAudioPlayerManager()
        val useCase = PlayTrackUseCase(
            settingsRepository = FakeSettingsRepository(volume = 80),
            trackRepository = FakeTrackRepository(
                trackResult = Err(Error.DatabaseFailure),
            ),
            audioPlayerManager = audioPlayerManager,
        )

        val result = useCase(trackId = 10L)

        assertEquals(Err(Error.DatabaseFailure), result)
        assertNull(audioPlayerManager.playedMp3List)
    }

    @Test
    fun 停止時はAudioPlayerManagerのstopを呼び出す() {
        val audioPlayerManager = FakeAudioPlayerManager()
        val useCase = PlayTrackUseCase(
            settingsRepository = FakeSettingsRepository(),
            trackRepository = FakeTrackRepository(),
            audioPlayerManager = audioPlayerManager,
        )

        val result = useCase.stop()

        assertEquals(Ok(Unit), result)
        assertEquals(true, audioPlayerManager.isStopped)
    }

    private fun createTrack(
        voiceList: List<ByteArray>,
        interval: Int = 10,
        playMode: PlayMode = PlayMode.NORMAL,
    ): Track {
        return Track(
            trackId = 10L,
            trackName = "track",
            characterType = CharacterType.ZUNDAMON,
            textList = listOf("text"),
            voiceList = voiceList,
            interval = interval,
            playMode = playMode,
            state = TrackState.PLAYABLE,
            isPinned = false,
        )
    }

    private class FakeSettingsRepository(
        private val volume: Int = 50,
    ) : ISettingsRepository {
        override fun getSettings(): Flow<AppSettings> =
            flowOf(AppSettings(volume = volume))

        override suspend fun saveVolume(volume: Int) =
            error("Not used")

        override suspend fun saveCheckPreRelease(checkPreRelease: Boolean) =
            error("Not used")

        override suspend fun saveUpdatePostponed(
            postponedAtMillis: Long,
            version: String,
        ) = error("Not used")
    }

    private class FakeTrackRepository(
        private val trackResult: Result<Track, Error> = Ok(
            createDefaultTrack(),
        ),
    ) : ITrackRepository {
        override suspend fun getTracks(): Result<List<Track>, Error> =
            error("Not used")

        override suspend fun getDraftTracks(): Result<List<Track>, Error> =
            error("Not used")

        override suspend fun getTrack(trackId: Long): Result<Track, Error> =
            trackResult

        override suspend fun addTrack(track: Track): Result<Long, Error> =
            error("Not used")

        override suspend fun updateTrack(track: Track): Result<Unit, Error> =
            error("Not used")

        override suspend fun deleteTrack(trackId: Long): Result<Unit, Error> =
            error("Not used")
    }

    private class FakeAudioPlayerManager(
        private val playResult: Result<Unit, Error> = Ok(Unit),
        private val stopResult: Result<Unit, Error> = Ok(Unit),
    ) : IAudioPlayerManager {
        var playedMp3List: List<ByteArray>? = null
        var playedIntervalSec: Int? = null
        var playedRandom: Boolean? = null
        var playedVolume: Int? = null
        var isStopped: Boolean = false

        override suspend fun play(
            mp3List: List<ByteArray>,
            intervalSec: Int,
            random: Boolean,
            volume: Int,
        ): Result<Unit, Error> {
            playedMp3List = mp3List
            playedIntervalSec = intervalSec
            playedRandom = random
            playedVolume = volume
            return playResult
        }

        override fun stop(): Result<Unit, Error> {
            isStopped = true
            return stopResult
        }
    }

    companion object {
        private fun createDefaultTrack(): Track {
            return Track(
                trackId = 10L,
                trackName = "track",
                characterType = CharacterType.ZUNDAMON,
                textList = listOf("text"),
                voiceList = listOf("voice".toByteArray()),
                interval = 10,
                playMode = PlayMode.NORMAL,
                state = TrackState.PLAYABLE,
                isPinned = false,
            )
        }
    }
}
