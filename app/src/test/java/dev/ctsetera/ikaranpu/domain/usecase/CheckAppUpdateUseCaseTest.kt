@file:Suppress("TestFunctionName", "NonAsciiCharacters")

package dev.ctsetera.ikaranpu.domain.usecase

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.ctsetera.ikaranpu.domain.model.AppRelease
import dev.ctsetera.ikaranpu.domain.model.AppSettings
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.repository.IAppUpdateRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CheckAppUpdateUseCaseTest {

    @Test
    fun 後で通知した同じバージョンは3日以内なら通知対象にならない() = runBlocking {
        val release = AppRelease(version = "v9.9.9", isPreRelease = false)
        val useCase = CheckAppUpdateUseCase(
            GetLatestAppReleaseUseCase(FakeAppUpdateRepository(release))
        )
        val settings = AppSettings(
            updatePostponedAtMillis = 1_000L,
            updatePostponedVersion = "v9.9.9",
        )

        val result = useCase(settings, nowMillis = 1_000L + 2L * 24L * 60L * 60L * 1000L)

        assertEquals(Ok(null), result)
    }

    @Test
    fun 後で通知したバージョンと異なる新しいバージョンは3日以内でも通知対象になる() = runBlocking {
        val release = AppRelease(version = "v9.9.10", isPreRelease = false)
        val useCase = CheckAppUpdateUseCase(
            GetLatestAppReleaseUseCase(FakeAppUpdateRepository(release))
        )
        val settings = AppSettings(
            updatePostponedAtMillis = 1_000L,
            updatePostponedVersion = "v9.9.9",
        )

        val result = useCase(settings, nowMillis = 2_000L)

        assertEquals(Ok(release), result)
    }

    private class FakeAppUpdateRepository(
        private val release: AppRelease?,
    ) : IAppUpdateRepository {
        override suspend fun getLatestRelease(
            includePreRelease: Boolean,
        ): Result<AppRelease?, Error> {
            return Ok(release)
        }
    }
}
