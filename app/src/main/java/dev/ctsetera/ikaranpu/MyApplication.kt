package dev.ctsetera.ikaranpu

import android.app.Application
import androidx.room.Room
import dev.ctsetera.ikaranpu.data.local.db.database.AppDatabase

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_db"
        ).build()
    }
}