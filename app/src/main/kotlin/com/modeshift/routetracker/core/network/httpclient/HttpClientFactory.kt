package com.modeshift.routetracker.core.network.httpclient

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
        baseUrl: String? = null,
        engine: HttpClientEngine = createDefaultEngine(),
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
                        response.status == HttpStatusCode.Companion.TooManyRequests -> true
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
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 3_000
            }

            install(Logging) {
                logger = HttpLogger()
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                        explicitNulls = false
                    },
                )
            }

            install(ContentEncoding) {
                gzip()
            }
        }

        return client
    }

    fun createDefaultEngine(): HttpClientEngine = OkHttp.create {
        config {
            callTimeout(10.seconds.toJavaDuration())
            connectTimeout(3.seconds.toJavaDuration())
        }
    }
}