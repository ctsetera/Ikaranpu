package dev.ctsetera.ikaranpu.data.remote.api

import com.google.gson.annotations.SerializedName

data class GitHubReleaseResponse(
    @SerializedName("tag_name")
    val tagName: String?,
    @SerializedName("draft")
    val draft: Boolean?,
    @SerializedName("prerelease")
    val preRelease: Boolean?,
)
