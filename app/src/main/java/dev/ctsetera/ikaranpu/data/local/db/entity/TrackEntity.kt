package dev.ctsetera.ikaranpu.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [
        Index(value = ["isActive", "updatedAt"]),
        Index(value = ["isActive", "isPinned", "updatedAt"])
    ]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "track_id")
    val trackId: Long,
    val trackName: String,
    val characterId: Int,
    val text1: String,
    val text2: String,
    val text3: String,
    val text4: String,
    val text5: String,
    val text6: String,
    val text7: String,
    val text8: String,
    val text9: String,
    val text10: String,
    val voice1: ByteArray,
    val voice2: ByteArray,
    val voice3: ByteArray,
    val voice4: ByteArray,
    val voice5: ByteArray,
    val voice6: ByteArray,
    val voice7: ByteArray,
    val voice8: ByteArray,
    val voice9: ByteArray,
    val voice10: ByteArray,
    val interval: Int,
    val playMode: Int,
    val isPinned: Boolean,
    val isActive: Boolean,
    val updatedAt: Long = System.currentTimeMillis(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackEntity

        if (trackId != other.trackId) return false
        if (trackName != other.trackName) return false
        if (characterId != other.characterId) return false
        if (text1 != other.text1) return false
        if (text2 != other.text2) return false
        if (text3 != other.text3) return false
        if (text4 != other.text4) return false
        if (text5 != other.text5) return false
        if (text6 != other.text6) return false
        if (text7 != other.text7) return false
        if (text8 != other.text8) return false
        if (text9 != other.text9) return false
        if (text10 != other.text10) return false
        if (!voice1.contentEquals(other.voice1)) return false
        if (!voice2.contentEquals(other.voice2)) return false
        if (!voice3.contentEquals(other.voice3)) return false
        if (!voice4.contentEquals(other.voice4)) return false
        if (!voice5.contentEquals(other.voice5)) return false
        if (!voice6.contentEquals(other.voice6)) return false
        if (!voice7.contentEquals(other.voice7)) return false
        if (!voice8.contentEquals(other.voice8)) return false
        if (!voice9.contentEquals(other.voice9)) return false
        if (!voice10.contentEquals(other.voice10)) return false
        if (interval != other.interval) return false
        if (playMode != other.playMode) return false
        if (isPinned != other.isPinned) return false
        if (isActive != other.isActive) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trackId.hashCode()
        result = 31 * result + trackName.hashCode()
        result = 31 * result + characterId
        result = 31 * result + text1.hashCode()
        result = 31 * result + text2.hashCode()
        result = 31 * result + text3.hashCode()
        result = 31 * result + text4.hashCode()
        result = 31 * result + text5.hashCode()
        result = 31 * result + text6.hashCode()
        result = 31 * result + text7.hashCode()
        result = 31 * result + text8.hashCode()
        result = 31 * result + text9.hashCode()
        result = 31 * result + text10.hashCode()
        result = 31 * result + voice1.contentHashCode()
        result = 31 * result + voice2.contentHashCode()
        result = 31 * result + voice3.contentHashCode()
        result = 31 * result + voice4.contentHashCode()
        result = 31 * result + voice5.contentHashCode()
        result = 31 * result + voice6.contentHashCode()
        result = 31 * result + voice7.contentHashCode()
        result = 31 * result + voice8.contentHashCode()
        result = 31 * result + voice9.contentHashCode()
        result = 31 * result + voice10.contentHashCode()
        result = 31 * result + interval
        result = 31 * result + playMode
        result = 31 * result + isPinned.hashCode()
        result = 31 * result + isActive.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}