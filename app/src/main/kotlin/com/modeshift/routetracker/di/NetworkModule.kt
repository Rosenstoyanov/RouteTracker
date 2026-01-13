package com.modeshift.routetracker.di

import com.modeshift.routetracker.BuildConfig
import com.modeshift.routetracker.core.network.httpclient.HttpClientFactory
import com.modeshift.routetracker.core.network.httpclient.HttpRequestExecutor
import com.modeshift.routetracker.core.network.status.NetworkConnectionMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(
        engine: HttpClientEngine,
        json: Json
    ): HttpClient {
        return HttpClientFactory.createHttpClient(
            json = json,
            baseUrl = BuildConfig.BASE_URL,
            engine = engine
        )
    }

    @Provides
    @Singleton
    fun provideHttpClientExecutor(
        json: Json,
        httpClient: HttpClient,
        networkMonitor: NetworkConnectionMonitor,
    ): HttpRequestExecutor {
        return HttpRequestExecutor(
            json = json,
            httpClient = httpClient,
            networkConnectionMonitor = networkMonitor,
        )
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}