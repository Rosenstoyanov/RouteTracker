package com.modeshift.routetracker.core.network.httpclient

import com.modeshift.routetracker.core.models.Resource
import com.modeshift.routetracker.core.network.status.NetworkConnectionMonitor
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import timber.log.Timber


class HttpRequestExecutor(
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
            val response = httpClient.request { block() }
            Resource.Success(response.body())
        } catch (exception: Exception) {
            Timber.e(exception)
            Resource.Failure("Something went wrong")
        }
    }
}