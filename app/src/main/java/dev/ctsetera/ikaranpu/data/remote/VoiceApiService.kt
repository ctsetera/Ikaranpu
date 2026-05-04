package dev.ctsetera.ikaranpu.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface VoiceApiService {

    // ① 音声生成
    @GET("v3/voicevox/synthesis")
    suspend fun synthesize(
        @Query("text") text: String,
        @Query("speaker") speaker: Int = 3,
    ): VoiceSynthesizeResponse

    // ② 音声ダウンロード（URL可変）
    @GET
    suspend fun downloadAudio(
        @Url url: String,
    ): Response<ResponseBody>

    // ③ ステータスチェック（URL可変）
    @GET
    suspend fun getAudioStatus(
        @Url url: String,
    ): VoiceSynthesizeStatusResponse
}