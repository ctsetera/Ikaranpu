package dev.ctsetera.ikaranpu.data.local.cache

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.ctsetera.ikaranpu.domain.model.AppSettings
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
        private val CHECK_PRE_RELEASE =
            booleanPreferencesKey("check_pre_release")
        private val UPDATE_POSTPONED_AT_MILLIS =
            longPreferencesKey("update_postponed_at_millis")
        private val UPDATE_POSTPONED_VERSION =
            stringPreferencesKey("update_postponed_version")
    }

    fun getSettings() = context.dataStore.data.map { prefs ->
        AppSettings(
            volume = prefs[VOICE_VOLUME] ?: 50,
            checkPreRelease = prefs[CHECK_PRE_RELEASE] ?: false,
            updatePostponedAtMillis = prefs[UPDATE_POSTPONED_AT_MILLIS] ?: 0L,
            updatePostponedVersion = prefs[UPDATE_POSTPONED_VERSION] ?: "",
        )
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

    suspend fun saveCheckPreRelease(checkPreRelease: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[CHECK_PRE_RELEASE] = checkPreRelease
        }
    }

    suspend fun saveUpdatePostponed(
        postponedAtMillis: Long,
        version: String,
    ) {
        context.dataStore.edit { prefs ->
            prefs[UPDATE_POSTPONED_AT_MILLIS] = postponedAtMillis
            prefs[UPDATE_POSTPONED_VERSION] = version
        }
    }
}
