package com.modeshift.routetracker.core.network.httpclient

import com.modeshift.routetracker.utils.Constants.NETWORK_CALL_TIMEOUT_IN_SECONDS
import com.modeshift.routetracker.utils.Constants.NETWORK_CONNECT_TIMEOUT_IN_SECONDS
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object HttpClientFactory {
    fun createHttpClient(
        json: Json,
        baseUrl: String? = null,
        engine: HttpClientEngine = createDefaultEngine()
    ): HttpClient {
        val client = HttpClient(engine) {
            expectSuccess = true

            defaultRequest {
                if (!baseUrl.isNullOrBlank()) {
                    url(baseUrl)
                }
            }

            install(HttpRequestRetry) {
                retryIf(2) { _, response ->
                    when {
                        response.status.value in 500..599 -> true
                        response.status == HttpStatusCode.TooManyRequests -> true
                        else -> false
                    }
                }
                retryOnException(maxRetries = 2, retryOnTimeout = true)
                exponentialDelay(
                    base = 1.0,
                    maxDelayMs = 2500,
                    randomizationMs = 500,
                    respectRetryAfterHeader = true
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = NETWORK_CALL_TIMEOUT_IN_SECONDS.seconds.inWholeMilliseconds
                connectTimeoutMillis =
                    NETWORK_CONNECT_TIMEOUT_IN_SECONDS.seconds.inWholeMilliseconds
            }

            install(Logging) {
                logger = HttpLogger()
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json(json)
            }

            install(ContentEncoding) {
                gzip()
            }
        }

        return client
    }

    fun createDefaultEngine(): HttpClientEngine = OkHttp.create {
        config {
            callTimeout(NETWORK_CALL_TIMEOUT_IN_SECONDS.seconds.toJavaDuration())
            connectTimeout(NETWORK_CONNECT_TIMEOUT_IN_SECONDS.seconds.toJavaDuration())
        }
    }
}