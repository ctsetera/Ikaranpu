package dev.ctsetera.ikaranpu.data.repository

import android.util.Log
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.data.remote.api.GitHubApiService
import dev.ctsetera.ikaranpu.domain.model.AppRelease
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.model.SemanticVersion
import dev.ctsetera.ikaranpu.domain.repository.IAppUpdateRepository

class AppUpdateRepository(
    private val service: GitHubApiService,
) : IAppUpdateRepository {

    override suspend fun getLatestRelease(
        includePreRelease: Boolean,
    ): Result<AppRelease?, Error> {
        return runCatching {
            service.getReleases()
                .asSequence()
                .filter { it.draft != true }
                .filter { includePreRelease || it.preRelease != true }
                .mapNotNull { release ->
                    val tagName = release.tagName ?: return@mapNotNull null
                    val version = SemanticVersion.parse(tagName) ?: return@mapNotNull null

                    ReleaseCandidate(
                        release = AppRelease(
                            version = tagName,
                            isPreRelease = release.preRelease == true,
                        ),
                        semanticVersion = version,
                    )
                }
                .maxByOrNull { it.semanticVersion }
                ?.release
        }.fold(
            onSuccess = { Ok(it) },
            onFailure = {
                Log.e(TAG, "Failed to fetch GitHub releases.", it)
                Err(Error.Unknown(it))
            },
        )
    }

    private data class ReleaseCandidate(
        val release: AppRelease,
        val semanticVersion: SemanticVersion,
    )

    companion object {
        private const val TAG = "AppUpdateRepository"
    }
}
