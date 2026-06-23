package dev.ctsetera.ikaranpu.data.remote.api

import retrofit2.http.GET

interface GitHubApiService {
    @GET("repos/ctsetera/Ikaranpu/releases")
    suspend fun getReleases(): List<GitHubReleaseResponse>
}
