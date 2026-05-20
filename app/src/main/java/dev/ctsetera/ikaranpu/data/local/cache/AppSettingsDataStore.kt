package dev.ctsetera.ikaranpu.data.local.cache

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "settings"
)

class AppSettingsDataStore(
    private val context: Context,
) {

    companion object {
        private val VOICE_VOLUME =
            intPreferencesKey("volume")
    }

    fun getVolume() = context.dataStore.data.map { prefs ->
        prefs[VOICE_VOLUME] ?: 50
    }

    suspend fun saveVolume(
        volume: Int,
    ) {

        context.dataStore.edit { prefs ->
            prefs[VOICE_VOLUME] = volume
        }
    }
}