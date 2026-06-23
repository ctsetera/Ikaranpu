package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import dev.ctsetera.ikaranpu.BuildConfig
import dev.ctsetera.ikaranpu.domain.model.AppRelease
import dev.ctsetera.ikaranpu.domain.model.AppSettings
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.SemanticVersion

class CheckAppUpdateUseCase(
    private val getLatestAppReleaseUseCase: GetLatestAppReleaseUseCase,
) {

    suspend operator fun invoke(
        settings: AppSettings,
        nowMillis: Long = System.currentTimeMillis(),
    ): Result<AppRelease?, Error> {
        return getLatestAppReleaseUseCase(settings.checkPreRelease)
            .map { latestRelease ->
                latestRelease
                    ?.takeIf { isNewerThanCurrent(it.version) }
                    ?.takeIf { !isPostponed(settings, it.version, nowMillis) }
            }
    }

    private fun isNewerThanCurrent(version: String): Boolean {
        val current = SemanticVersion.parse(BuildConfig.VERSION_NAME) ?: return true
        val latest = SemanticVersion.parse(version) ?: return false

        return latest > current
    }

    private fun isPostponed(
        settings: AppSettings,
        latestVersion: String,
        nowMillis: Long,
    ): Boolean {
        val postponeIntervalMillis = 3L * 24L * 60L * 60L * 1000L
        val elapsedMillis = nowMillis - settings.updatePostponedAtMillis

        return settings.updatePostponedVersion == latestVersion &&
                settings.updatePostponedAtMillis > 0L &&
                elapsedMillis in 0 until postponeIntervalMillis
    }
}
