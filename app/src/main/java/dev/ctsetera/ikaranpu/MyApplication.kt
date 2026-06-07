package dev.ctsetera.ikaranpu

import android.app.Application
import androidx.room.Room
import dev.ctsetera.ikaranpu.data.local.db.database.AppDatabase
import dev.ctsetera.ikaranpu.di.AppContainer

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_db"
        ).build()

        appContainer = AppContainer(
            context = applicationContext,
            database = database,
        )
    }
}
