package dev.ctsetera.ikaranpu.di

import android.content.Context
import dev.ctsetera.ikaranpu.data.audio.AudioPlayerManager
import dev.ctsetera.ikaranpu.data.local.cache.AppSettingsDataStore
import dev.ctsetera.ikaranpu.data.local.db.database.AppDatabase
import dev.ctsetera.ikaranpu.data.remote.api.GitHubApiClient
import dev.ctsetera.ikaranpu.data.remote.api.GitHubApiService
import dev.ctsetera.ikaranpu.data.remote.api.VoiceApiClient
import dev.ctsetera.ikaranpu.data.remote.api.VoiceApiService
import dev.ctsetera.ikaranpu.data.repository.AppUpdateRepository
import dev.ctsetera.ikaranpu.data.repository.SettingsRepository
import dev.ctsetera.ikaranpu.data.repository.TrackRepository
import dev.ctsetera.ikaranpu.data.repository.VoiceRepository
import dev.ctsetera.ikaranpu.domain.usecase.AddTrackUseCase
import dev.ctsetera.ikaranpu.domain.usecase.CheckAppUpdateUseCase
import dev.ctsetera.ikaranpu.domain.usecase.DeleteTrackUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetLatestAppReleaseUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetDraftListUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetSettingsUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackByTrackIdUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackListUseCase
import dev.ctsetera.ikaranpu.domain.usecase.PlayTrackUseCase
import dev.ctsetera.ikaranpu.domain.usecase.SaveSettingUseCase
import dev.ctsetera.ikaranpu.domain.usecase.UpdateTrackUseCase

class AppContainer(
    private val context: Context,
    database: AppDatabase,
) {
    private val trackRepository by lazy {
        TrackRepository(database.trackDao())
    }

    private val settingsRepository by lazy {
        SettingsRepository(AppSettingsDataStore(context))
    }

    private val voiceRepository by lazy {
        VoiceRepository(
            VoiceApiClient.retrofit.create(VoiceApiService::class.java)
        )
    }

    private val appUpdateRepository by lazy {
        AppUpdateRepository(
            GitHubApiClient.retrofit.create(GitHubApiService::class.java)
        )
    }

    val getTrackListUseCase by lazy {
        GetTrackListUseCase(trackRepository)
    }

    val getDraftListUseCase by lazy {
        GetDraftListUseCase(trackRepository)
    }

    val deleteTrackUseCase by lazy {
        DeleteTrackUseCase(trackRepository)
    }

    val getTrackByTrackIdUseCase by lazy {
        GetTrackByTrackIdUseCase(trackRepository)
    }

    val getSettingsUseCase by lazy {
        GetSettingsUseCase(settingsRepository)
    }

    val saveSettingUseCase by lazy {
        SaveSettingUseCase(settingsRepository)
    }

    val getLatestAppReleaseUseCase by lazy {
        GetLatestAppReleaseUseCase(appUpdateRepository)
    }

    val checkAppUpdateUseCase by lazy {
        CheckAppUpdateUseCase(getLatestAppReleaseUseCase)
    }

    fun createAddTrackUseCase(): AddTrackUseCase {
        return AddTrackUseCase(
            trackRepository = trackRepository,
            voiceRepository = voiceRepository,
        )
    }

    fun createUpdateTrackUseCase(): UpdateTrackUseCase {
        return UpdateTrackUseCase(
            trackRepository = trackRepository,
            voiceRepository = voiceRepository,
        )
    }

    fun createPlayTrackUseCase(): PlayTrackUseCase {
        return PlayTrackUseCase(
            settingsRepository = settingsRepository,
            trackRepository = trackRepository,
            audioPlayerManager = AudioPlayerManager(context),
        )
    }
}
