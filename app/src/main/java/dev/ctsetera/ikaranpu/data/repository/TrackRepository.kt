package dev.ctsetera.ikaranpu.data.repository

import android.util.Log
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.data.local.db.dao.TrackDao
import dev.ctsetera.ikaranpu.data.local.db.entity.TrackEntity
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.domain.repository.ITrackRepository

class TrackRepository(private val trackDao: TrackDao) : ITrackRepository {
    override suspend fun getTracks(): Result<List<Track>, Error> {
        return runCatching {
            trackDao.getTracksOderByIsPinnedAscAndUpdatedAtDesc()
        }.fold(
            onSuccess = { tracks ->
                if (tracks.isNotEmpty()) {
                    Ok(tracks.map { convertEntityToModel(it) })
                } else {
                    Log.w(this::class.java.simpleName, "TRACK NOT FOUND")
                    Err(Error.TrackNotFound)
                }
            },
            onFailure = {
                Log.e(this::class.java.simpleName, "DATABASE FAILURE\n" + it.stackTraceToString())
                Err(Error.DatabaseFailure)
            }
        )
    }

    override suspend fun getDraftTracks(): Result<List<Track>, Error> {
        return runCatching {
            trackDao.getDraftTracksOderByUpdatedAtDesc()
        }.fold(
            onSuccess = { drafts ->
                if (drafts.isNotEmpty()) {
                    Ok(drafts.map { convertEntityToModel(it) })
                } else {
                    Log.w(this::class.java.simpleName, "TRACK NOT FOUND")
                    Err(Error.TrackNotFound)
                }
            },
            onFailure = {
                Log.e(this::class.java.simpleName, "DATABASE FAILURE\n" + it.stackTraceToString())
                Err(Error.DatabaseFailure)
            }
        )
    }

    override suspend fun getTrack(trackId: Long): Result<Track, Error> {
        return runCatching {
            trackDao.findByTrackId(trackId = trackId)
        }.fold(
            onSuccess = { track ->
                if (track == null) {
                    Log.w(this::class.java.simpleName, "TRACK NOT FOUND")
                    Err(Error.TrackNotFound)
                } else {
                    Ok(convertEntityToModel(track))
                }
            },
            onFailure = {
                Log.e(this::class.java.simpleName, "DATABASE FAILURE\n" + it.stackTraceToString())
                Err(Error.DatabaseFailure)
            }
        )
    }

    override suspend fun addTrack(track: Track): Result<Long, Error> {
        return runCatching {
            trackDao.insert(convertModelToEntity(track))
        }.fold(
            onSuccess = {
                Ok(it)
            },
            onFailure = {
                Log.e(this::class.java.simpleName, "DATABASE FAILURE\n" + it.stackTraceToString())
                Err(Error.DatabaseFailure)
            }
        )
    }

    override suspend fun updateTrack(track: Track): Result<Unit, Error> {
        return runCatching {
            trackDao.update(convertModelToEntity(track))
        }.fold(
            onSuccess = {
                Ok(it)
            },
            onFailure = {
                Log.e(this::class.java.simpleName, "DATABASE FAILURE\n" + it.stackTraceToString())
                Err(Error.DatabaseFailure)
            }
        )
    }

    override suspend fun deleteTrack(trackId: Long): Result<Unit, Error> {
        return runCatching {
            trackDao.deleteByTrackId(trackId)
        }.fold(
            onSuccess = {
                Ok(it)
            },
            onFailure = {
                Log.e(this::class.java.simpleName, "DATABASE FAILURE\n" + it.stackTraceToString())
                Err(Error.DatabaseFailure)
            }
        )
    }

    private fun convertEntityToModel(trackEntity: TrackEntity): Track {
        return Track(
            trackId = trackEntity.trackId,
            trackName = trackEntity.trackName,
            characterType = when (trackEntity.characterId) {
                0 -> CharacterType.ZUNDAMON
                1 -> CharacterType.METAN
                else -> CharacterType.ZUNDAMON
            },
            textList = listOf(
                trackEntity.text1,
                trackEntity.text2,
                trackEntity.text3,
                trackEntity.text4,
                trackEntity.text5,
                trackEntity.text6,
                trackEntity.text7,
                trackEntity.text8,
                trackEntity.text9,
                trackEntity.text10
            ).filter { it.isNotEmpty() },
            voiceList = listOf(
                trackEntity.voice1,
                trackEntity.voice2,
                trackEntity.voice3,
                trackEntity.voice4,
                trackEntity.voice5,
                trackEntity.voice6,
                trackEntity.voice7,
                trackEntity.voice8,
                trackEntity.voice9,
                trackEntity.voice10
            ).filter { it.isNotEmpty() },
            interval = trackEntity.interval,
            playMode = when (trackEntity.playMode) {
                0 -> PlayMode.NORMAL
                1 -> PlayMode.RANDOM
                else -> PlayMode.NORMAL
            },
            state = if (trackEntity.isActive) TrackState.PLAYABLE else TrackState.DRAFT,
            isPinned = trackEntity.isPinned,
        )
    }

    private fun convertModelToEntity(track: Track): TrackEntity {
        val textArray = arrayOfNulls<String>(10)
        val voiceArray = arrayOfNulls<ByteArray>(10)

        track.textList
            .mapIndexed { index, text -> textArray[index] = text }
        track.voiceList
            .mapIndexed { index, voice -> voiceArray[index] = voice }

        val textList = textArray.map { it ?: "" }.toList()
        val voiceList = voiceArray.map { it ?: byteArrayOf() }.toList()

        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            characterId = when (track.characterType) {
                CharacterType.ZUNDAMON -> 0
                CharacterType.METAN -> 1
            },
            text1 = textList[0],
            text2 = textList[1],
            text3 = textList[2],
            text4 = textList[3],
            text5 = textList[4],
            text6 = textList[5],
            text7 = textList[6],
            text8 = textList[7],
            text9 = textList[8],
            text10 = textList[9],
            voice1 = voiceList[0],
            voice2 = voiceList[1],
            voice3 = voiceList[2],
            voice4 = voiceList[3],
            voice5 = voiceList[4],
            voice6 = voiceList[5],
            voice7 = voiceList[6],
            voice8 = voiceList[7],
            voice9 = voiceList[8],
            voice10 = voiceList[9],
            interval = track.interval,
            playMode = when (track.playMode) {
                PlayMode.NORMAL -> 0
                PlayMode.RANDOM -> 1
            },
            isActive = track.state == TrackState.PLAYABLE,
            isPinned = track.isPinned,
        )
    }
}