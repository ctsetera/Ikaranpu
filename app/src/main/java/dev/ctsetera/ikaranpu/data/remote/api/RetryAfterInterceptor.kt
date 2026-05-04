package dev.ctsetera.ikaranpu.data.remote.api

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class RetryAfterInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)

        var retryCount = 0

        while (response.code == 429) {
            val retryAfter = response.header("Retry-After")

            val waitMillis = parseRetryAfterToMillis(retryAfter)

            response.close()

            try {
                Thread.sleep(waitMillis)
            } catch (e: InterruptedException) {
                break
            }

            retryCount++
            response = chain.proceed(request)
        }

        return response
    }

    private fun parseRetryAfterToMillis(header: String?): Long {
        return try {
            header?.toLong()?.let { TimeUnit.SECONDS.toMillis(it) }
                ?: 1000L
        } catch (e: Exception) {
            1000L
        }
    }
}