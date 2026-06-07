@file:Suppress("TestFunctionName", "NonAsciiCharacters")

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
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TrackSaveUseCaseTest {
    @Test
    fun 追加の通常保存では音声生成してトラックを保存する() = runBlocking {
        val trackRepository = FakeTrackRepository()
        val useCase = AddTrackUseCase(
            trackRepository = trackRepository,
            voiceRepository = FakeVoiceRepository(),
        )

        val progress = async(start = CoroutineStart.UNDISPATCHED) {
            useCase.progressFlow.take(4).toList()
        }
        val result = useCase(
            trackName = "track",
            characterType = CharacterType.ZUNDAMON,
            textList = listOf("text", ""),
            interval = 10,
            playMode = PlayMode.NORMAL,
            state = TrackState.PLAYABLE,
        )

        assertEquals(Ok(1L), result)
        assertVoiceListEquals(
            expected = listOf("text".toByteArray()),
            actual = trackRepository.addedTrack?.voiceList,
        )
        assertEquals(
            listOf(
                TrackProgress.Downloading(current = 1, total = 1),
                TrackProgress.Downloaded(current = 1, total = 1),
                TrackProgress.Saving,
                TrackProgress.Completed,
            ),
            progress.await(),
        )
    }

    @Test
    fun 追加の下書き保存では音声生成せずトラックを保存する() = runBlocking {
        val voiceRepository = FakeVoiceRepository()
        val trackRepository = FakeTrackRepository()
        val useCase = AddTrackUseCase(
            trackRepository = trackRepository,
            voiceRepository = voiceRepository,
        )

        val progress = async(start = CoroutineStart.UNDISPATCHED) {
            useCase.progressFlow.take(2).toList()
        }
        val result = useCase(
            trackName = "track",
            characterType = CharacterType.ZUNDAMON,
            textList = listOf("text"),
            interval = 10,
            playMode = PlayMode.NORMAL,
            state = TrackState.DRAFT,
        )

        assertEquals(Ok(1L), result)
        assertEquals(emptyList<String>(), voiceRepository.requestedTexts)
        assertEquals(emptyList<ByteArray>(), trackRepository.addedTrack?.voiceList)
        assertEquals(
            listOf(
                TrackProgress.Saving,
                TrackProgress.Completed,
            ),
            progress.await(),
        )
    }

    @Test
    fun 音声生成に失敗するとトラックを保存しない() = runBlocking {
        val trackRepository = FakeTrackRepository()
        val useCase = AddTrackUseCase(
            trackRepository = trackRepository,
            voiceRepository = FakeVoiceRepository(result = Err(Error.ApiServerFailure)),
        )

        val progress = async(start = CoroutineStart.UNDISPATCHED) {
            useCase.progressFlow.take(2).toList()
        }
        val result = useCase(
            trackName = "track",
            characterType = CharacterType.ZUNDAMON,
            textList = listOf("text"),
            interval = 10,
            playMode = PlayMode.NORMAL,
            state = TrackState.PLAYABLE,
        )

        assertEquals(Err(Error.ApiServerFailure), result)
        assertNull(trackRepository.addedTrack)
        assertEquals(
            listOf(
                TrackProgress.Downloading(current = 1, total = 1),
                TrackProgress.Failed(Error.ApiServerFailure),
            ),
            progress.await(),
        )
    }

    @Test
    fun 更新では指定したトラックIDでトラックを保存する() = runBlocking {
        val trackRepository = FakeTrackRepository()
        val useCase = UpdateTrackUseCase(
            trackRepository = trackRepository,
            voiceRepository = FakeVoiceRepository(),
        )

        val progress = async(start = CoroutineStart.UNDISPATCHED) {
            useCase.progressFlow.take(4).toList()
        }
        val result = useCase(
            trackId = 10L,
            trackName = "track",
            characterType = CharacterType.ZUNDAMON,
            textList = listOf("text"),
            interval = 10,
            playMode = PlayMode.NORMAL,
            state = TrackState.PLAYABLE,
        )

        assertEquals(Ok(Unit), result)
        assertEquals(10L, trackRepository.updatedTrack?.trackId)
        assertVoiceListEquals(
            expected = listOf("text".toByteArray()),
            actual = trackRepository.updatedTrack?.voiceList,
        )
        assertEquals(
            listOf(
                TrackProgress.Downloading(current = 1, total = 1),
                TrackProgress.Downloaded(current = 1, total = 1),
                TrackProgress.Saving,
                TrackProgress.Completed,
            ),
            progress.await(),
        )
    }

    @Test
    fun 保存に失敗するとFailedを通知する() = runBlocking {
        val useCase = AddTrackUseCase(
            trackRepository = FakeTrackRepository(addResult = Err(Error.DatabaseFailure)),
            voiceRepository = FakeVoiceRepository(),
        )

        val progress = async(start = CoroutineStart.UNDISPATCHED) {
            useCase.progressFlow.take(4).toList()
        }
        val result = useCase(
            trackName = "track",
            characterType = CharacterType.ZUNDAMON,
            textList = listOf("text"),
            interval = 10,
            playMode = PlayMode.NORMAL,
            state = TrackState.PLAYABLE,
        )

        assertEquals(Err(Error.DatabaseFailure), result)
        assertEquals(
            listOf(
                TrackProgress.Downloading(current = 1, total = 1),
                TrackProgress.Downloaded(current = 1, total = 1),
                TrackProgress.Saving,
                TrackProgress.Failed(Error.DatabaseFailure),
            ),
            progress.await(),
        )
    }

    private class FakeVoiceRepository(
        private val result: Result<ByteArray, Error> = Ok("text".toByteArray()),
    ) : IVoiceRepository {
        val requestedTexts = mutableListOf<String>()

        override suspend fun generateAndDownload(
            text: String,
            characterType: CharacterType,
        ): Result<ByteArray, Error> {
            requestedTexts.add(text)
            return result
        }
    }

    private fun assertVoiceListEquals(
        expected: List<ByteArray>,
        actual: List<ByteArray>?,
    ) {
        assertEquals(expected.size, actual?.size)
        expected.zip(actual ?: emptyList()).forEach { (expectedVoice, actualVoice) ->
            assertArrayEquals(expectedVoice, actualVoice)
        }
    }

    private class FakeTrackRepository(
        private val addResult: Result<Long, Error> = Ok(1L),
        private val updateResult: Result<Unit, Error> = Ok(Unit),
    ) : ITrackRepository {
        var addedTrack: Track? = null
        var updatedTrack: Track? = null

        override suspend fun getTracks(): Result<List<Track>, Error> =
            error("Not used")

        override suspend fun getDraftTracks(): Result<List<Track>, Error> =
            error("Not used")

        override suspend fun getTrack(trackId: Long): Result<Track, Error> =
            error("Not used")

        override suspend fun addTrack(track: Track): Result<Long, Error> {
            addedTrack = track
            return addResult
        }

        override suspend fun updateTrack(track: Track): Result<Unit, Error> {
            updatedTrack = track
            return updateResult
        }

        override suspend fun deleteTrack(trackId: Long): Result<Unit, Error> =
            error("Not used")
    }
}
