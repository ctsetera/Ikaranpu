@file:Suppress("TestFunctionName", "NonAsciiCharacters")

package dev.ctsetera.ikaranpu.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import dev.ctsetera.ikaranpu.data.local.db.dao.TrackDao
import dev.ctsetera.ikaranpu.data.local.db.entity.TrackEntity
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackRepositoryTest {
    @Test
    fun 通常トラック一覧が空なら空一覧を返す() = runBlocking {
        val repository = TrackRepository(FakeTrackDao())

        val result = repository.getTracks()

        assertEquals(Ok(emptyList<Track>()), result)
    }

    @Test
    fun 下書き一覧が空なら空一覧を返す() = runBlocking {
        val repository = TrackRepository(FakeTrackDao())

        val result = repository.getDraftTracks()

        assertEquals(Ok(emptyList<Track>()), result)
    }

    @Test
    fun 通常トラック一覧をモデルへ変換して返す() = runBlocking {
        val entity = createEntity(isActive = true)
        val repository = TrackRepository(FakeTrackDao(tracks = listOf(entity)))

        val result = repository.getTracks()

        assertTrue(result is Ok)
        assertTrackMatches(entity, (result as Ok).value.single())
    }

    @Test
    fun 下書き一覧をモデルへ変換して返す() = runBlocking {
        val entity = createEntity(isActive = false)
        val repository = TrackRepository(FakeTrackDao(drafts = listOf(entity)))

        val result = repository.getDraftTracks()

        assertTrue(result is Ok)
        val track = (result as Ok).value.single()
        assertTrackMatches(entity, track)
        assertEquals(TrackState.DRAFT, track.state)
    }

    @Test
    fun 指定したトラックが存在しなければTrackNotFoundを返す() = runBlocking {
        val repository = TrackRepository(FakeTrackDao())

        val result = repository.getTrack(trackId = 99L)

        assertEquals(Err(Error.TrackNotFound), result)
    }

    @Test
    fun 指定したトラックをモデルへ変換して返す() = runBlocking {
        val entity = createEntity(trackId = 10L)
        val repository = TrackRepository(FakeTrackDao(foundTrack = entity))

        val result = repository.getTrack(trackId = 10L)

        assertTrue(result is Ok)
        assertTrackMatches(entity, (result as Ok).value)
    }

    @Test
    fun 追加時にモデルをエンティティへ変換してDAOへ渡す() = runBlocking {
        val dao = FakeTrackDao(insertResult = 42L)
        val repository = TrackRepository(dao)
        val track = createTrack()

        val result = repository.addTrack(track)

        assertEquals(Ok(42L), result)
        assertEntityMatches(track, dao.insertedTrack)
    }

    @Test
    fun 更新時にモデルをエンティティへ変換してDAOへ渡す() = runBlocking {
        val dao = FakeTrackDao()
        val repository = TrackRepository(dao)
        val track = createTrack()

        val result = repository.updateTrack(track)

        assertEquals(Ok(Unit), result)
        assertEntityMatches(track, dao.updatedTrack)
    }

    @Test
    fun 削除時に指定したトラックIDをDAOへ渡す() = runBlocking {
        val dao = FakeTrackDao()
        val repository = TrackRepository(dao)

        val result = repository.deleteTrack(trackId = 15L)

        assertEquals(Ok(Unit), result)
        assertEquals(15L, dao.deletedTrackId)
    }

    @Test
    fun 通常トラック一覧取得時にDAOで例外が発生したらDatabaseFailureを返す() = runBlocking {
        val repository = TrackRepository(
            FakeTrackDao(getTracksException = RuntimeException())
        )

        val result = repository.getTracks()

        assertEquals(Err(Error.DatabaseFailure), result)
    }

    @Test
    fun 下書き一覧取得時にDAOで例外が発生したらDatabaseFailureを返す() = runBlocking {
        val repository = TrackRepository(
            FakeTrackDao(getDraftTracksException = RuntimeException())
        )

        val result = repository.getDraftTracks()

        assertEquals(Err(Error.DatabaseFailure), result)
    }

    @Test
    fun トラック取得時にDAOで例外が発生したらDatabaseFailureを返す() = runBlocking {
        val repository = TrackRepository(
            FakeTrackDao(findByTrackIdException = RuntimeException())
        )

        val result = repository.getTrack(trackId = 1L)

        assertEquals(Err(Error.DatabaseFailure), result)
    }

    @Test
    fun トラック追加時にDAOで例外が発生したらDatabaseFailureを返す() = runBlocking {
        val repository = TrackRepository(
            FakeTrackDao(insertException = RuntimeException())
        )

        val result = repository.addTrack(createTrack())

        assertEquals(Err(Error.DatabaseFailure), result)
    }

    @Test
    fun トラック更新時にDAOで例外が発生したらDatabaseFailureを返す() = runBlocking {
        val repository = TrackRepository(
            FakeTrackDao(updateException = RuntimeException())
        )

        val result = repository.updateTrack(createTrack())

        assertEquals(Err(Error.DatabaseFailure), result)
    }

    @Test
    fun トラック削除時にDAOで例外が発生したらDatabaseFailureを返す() = runBlocking {
        val repository = TrackRepository(
            FakeTrackDao(deleteException = RuntimeException())
        )

        val result = repository.deleteTrack(trackId = 1L)

        assertEquals(Err(Error.DatabaseFailure), result)
    }

    private fun assertTrackMatches(entity: TrackEntity, track: Track) {
        assertEquals(entity.trackId, track.trackId)
        assertEquals(entity.trackName, track.trackName)
        assertEquals(CharacterType.METAN, track.characterType)
        assertEquals(listOf(entity.text1, entity.text2), track.textList)
        assertEquals(2, track.voiceList.size)
        assertArrayEquals(entity.voice1, track.voiceList[0])
        assertArrayEquals(entity.voice2, track.voiceList[1])
        assertEquals(entity.interval, track.interval)
        assertEquals(PlayMode.RANDOM, track.playMode)
        assertEquals(entity.isPinned, track.isPinned)
    }

    private fun assertEntityMatches(track: Track, entity: TrackEntity?) {
        requireNotNull(entity)
        assertEquals(track.trackId, entity.trackId)
        assertEquals(track.trackName, entity.trackName)
        assertEquals(1, entity.characterId)
        assertEquals(track.textList[0], entity.text1)
        assertEquals(track.textList[1], entity.text2)
        assertEquals("", entity.text3)
        assertArrayEquals(track.voiceList[0], entity.voice1)
        assertArrayEquals(track.voiceList[1], entity.voice2)
        assertArrayEquals(byteArrayOf(), entity.voice3)
        assertEquals(track.interval, entity.interval)
        assertEquals(1, entity.playMode)
        assertEquals(track.isPinned, entity.isPinned)
        assertEquals(true, entity.isActive)
    }

    private fun createTrack(): Track {
        return Track(
            trackId = 5L,
            trackName = "トラック",
            characterType = CharacterType.METAN,
            textList = listOf("テキスト1", "テキスト2"),
            voiceList = listOf("voice1".toByteArray(), "voice2".toByteArray()),
            interval = 30,
            playMode = PlayMode.RANDOM,
            state = TrackState.PLAYABLE,
            isPinned = true,
        )
    }

    private fun createEntity(
        trackId: Long = 1L,
        isActive: Boolean = true,
    ): TrackEntity {
        return TrackEntity(
            trackId = trackId,
            trackName = "トラック",
            characterId = 1,
            text1 = "テキスト1",
            text2 = "テキスト2",
            text3 = "",
            text4 = "",
            text5 = "",
            text6 = "",
            text7 = "",
            text8 = "",
            text9 = "",
            text10 = "",
            voice1 = "voice1".toByteArray(),
            voice2 = "voice2".toByteArray(),
            voice3 = byteArrayOf(),
            voice4 = byteArrayOf(),
            voice5 = byteArrayOf(),
            voice6 = byteArrayOf(),
            voice7 = byteArrayOf(),
            voice8 = byteArrayOf(),
            voice9 = byteArrayOf(),
            voice10 = byteArrayOf(),
            interval = 30,
            playMode = 1,
            isPinned = true,
            isActive = isActive,
            updatedAt = 100L,
        )
    }

    private class FakeTrackDao(
        private val tracks: List<TrackEntity> = emptyList(),
        private val drafts: List<TrackEntity> = emptyList(),
        private val foundTrack: TrackEntity? = null,
        private val insertResult: Long = 1L,
        private val getTracksException: Throwable? = null,
        private val getDraftTracksException: Throwable? = null,
        private val findByTrackIdException: Throwable? = null,
        private val insertException: Throwable? = null,
        private val updateException: Throwable? = null,
        private val deleteException: Throwable? = null,
    ) : TrackDao {
        var insertedTrack: TrackEntity? = null
        var updatedTrack: TrackEntity? = null
        var deletedTrackId: Long? = null

        override suspend fun insert(user: TrackEntity): Long {
            insertException?.let { throw it }
            insertedTrack = user
            return insertResult
        }

        override suspend fun update(user: TrackEntity) {
            updateException?.let { throw it }
            updatedTrack = user
        }

        override suspend fun deleteByTrackId(trackId: Long) {
            deleteException?.let { throw it }
            deletedTrackId = trackId
        }

        override suspend fun getAll(): List<TrackEntity> = tracks

        override suspend fun getTracksOderByIsPinnedAscAndUpdatedAtDesc(): List<TrackEntity> {
            getTracksException?.let { throw it }
            return tracks
        }

        override suspend fun getDraftTracksOderByUpdatedAtDesc(): List<TrackEntity> {
            getDraftTracksException?.let { throw it }
            return drafts
        }

        override suspend fun findByTrackId(trackId: Long): TrackEntity? {
            findByTrackIdException?.let { throw it }
            return foundTrack
        }
    }
}
