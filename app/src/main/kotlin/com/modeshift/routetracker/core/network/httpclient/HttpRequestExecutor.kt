package com.modeshift.routetracker.core.network.httpclient

import com.modeshift.routetracker.core.models.Resource
import com.modeshift.routetracker.core.network.status.NetworkConnectionMonitor
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import kotlinx.serialization.json.Json
import timber.log.Timber

class HttpRequestExecutor(
    val json: Json,
    val httpClient: HttpClient,
    val networkConnectionMonitor: NetworkConnectionMonitor,
) {
    suspend inline fun <reified Data> execute(
        block: HttpRequestBuilder.() -> Unit,
    ): Resource<Data> {
        if (!networkConnectionMonitor.isConnected()) {
            return Resource.Failure("No internet connection")
        }

        return try {
            val rawResponse = httpClient.request { block() }.body<String>()

            if (rawResponse.isBlank()) {
                return if (Data::class == Unit::class) {
                    Resource.Success(Unit as Data)
                } else {
                    Resource.Failure("Empty response body")
                }
            }

            val unescapedJson = try {
                json.decodeFromString<String>(rawResponse)
            } catch (_: Exception) {
                rawResponse
            }
            val response = json.decodeFromString<Data>(unescapedJson)
            Resource.Success(response)
        } catch (exception: Exception) {
            Timber.e(exception)
            Resource.Failure("Something went wrong")
        }
    }
}