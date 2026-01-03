package dev.ctsetera.ikaranpu.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.data.local.db.dao.TrackDao
import dev.ctsetera.ikaranpu.data.local.db.entity.TrackEntity
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackError
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.domain.repository.ITrackRepository

class TrackRepository(private val trackDao: TrackDao) : ITrackRepository {
    override suspend fun getTracks(): Result<List<Track>, TrackError> {
        return runCatching {
            trackDao.getTracks()
        }.fold(
            onSuccess = { tracks ->
                if (tracks.isNotEmpty()) {
                    Ok(tracks.map { convertEntityToModel(it) })
                } else {
                    Err(TrackError.NotFound)
                }
            },
            onFailure = {
                Err(TrackError.DatabaseFailure)
            }
        )
    }

    override suspend fun getDraftTracks(): Result<List<Track>, TrackError> {
        return runCatching {
            trackDao.getDraftTracks()
        }.fold(
            onSuccess = { drafts ->
                if (drafts.isNotEmpty()) {
                    Ok(drafts.map { convertEntityToModel(it) })
                } else {
                    Err(TrackError.NotFound)
                }
            },
            onFailure = {
                Err(TrackError.DatabaseFailure)
            }
        )
    }

    override suspend fun getTrack(trackId: Long): Result<Track, TrackError> {
        return runCatching {
            trackDao.findByTrackId(trackId = trackId)
        }.fold(
            onSuccess = { track ->
                track?.let {
                    Ok(convertEntityToModel(it))
                } ?: Err(TrackError.NotFound)
            },
            onFailure = {
                Err(TrackError.DatabaseFailure)
            }
        )
    }

    override suspend fun addTrack(track: Track): Result<Long, TrackError> {
        return runCatching {
            trackDao.insert(convertModelToEntity(track))
        }.fold(
            onSuccess = {
                Ok(it)
            },
            onFailure = {
                Err(TrackError.DatabaseFailure)
            }
        )
    }

    override suspend fun updateTrack(track: Track): Result<Unit, TrackError> {
        return runCatching {
            trackDao.update(convertModelToEntity(track))
        }.fold(
            onSuccess = {
                Ok(it)
            },
            onFailure = {
                Err(TrackError.DatabaseFailure)
            }
        )
    }

    override suspend fun deleteTrack(trackId: Long): Result<Unit, TrackError> {
        return runCatching {
            trackDao.deleteByTrackId(trackId)
        }.fold(
            onSuccess = {
                Ok(it)
            },
            onFailure = {
                Err(TrackError.DatabaseFailure)
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
            textList = listOfNotNull(
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
            ),
            voiceList = listOfNotNull(
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
            ),
            interval = trackEntity.interval,
            playMode = when (trackEntity.playMode) {
                0 -> PlayMode.NORMAL
                1 -> PlayMode.RANDOM
                else -> PlayMode.NORMAL
            },
            startText = trackEntity.startText ?: "",
            startVoice = byteArrayOf(),
            endText = trackEntity.endText ?: "",
            endVoice = byteArrayOf(),
            state = if (trackEntity.isActive) TrackState.PLAYABLE else TrackState.DRAFT,
        )
    }

    private fun convertModelToEntity(track: Track): TrackEntity {
        val textArray = arrayOfNulls<String>(10)
        val voiceArray = arrayOfNulls<ByteArray>(10)

        track.textList
            .filter { text -> text.isNotEmpty() }
            .mapIndexed { index, text -> textArray[index] = text }
        track.voiceList
            .mapIndexed { index, voice -> voiceArray[index] = voice }

        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            characterId = when (track.characterType) {
                CharacterType.ZUNDAMON -> 0
                CharacterType.METAN -> 1
            },
            text1 = textArray[0],
            text2 = textArray[1],
            text3 = textArray[2],
            text4 = textArray[3],
            text5 = textArray[4],
            text6 = textArray[5],
            text7 = textArray[6],
            text8 = textArray[7],
            text9 = textArray[8],
            text10 = textArray[9],
            voice1 = voiceArray[0],
            voice2 = voiceArray[1],
            voice3 = voiceArray[2],
            voice4 = voiceArray[3],
            voice5 = voiceArray[4],
            voice6 = voiceArray[5],
            voice7 = voiceArray[6],
            voice8 = voiceArray[7],
            voice9 = voiceArray[8],
            voice10 = voiceArray[9],
            interval = track.interval,
            playMode = when (track.playMode) {
                PlayMode.NORMAL -> 0
                PlayMode.RANDOM -> 1
            },
            startText = track.startText,
            endText = track.endText,
            isActive = track.state == TrackState.PLAYABLE
        )
    }
}