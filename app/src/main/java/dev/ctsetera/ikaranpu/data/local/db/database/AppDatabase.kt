package dev.ctsetera.ikaranpu.data.local.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.ctsetera.ikaranpu.data.local.db.dao.TrackDao
import dev.ctsetera.ikaranpu.data.local.db.entity.TrackEntity

@Database(
    entities = [TrackEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}