package com.modeshift.routetracker.di

import com.modeshift.routetracker.core.network.httpclient.HttpClientFactory
import com.modeshift.routetracker.core.network.httpclient.HttpRequestExecutor
import com.modeshift.routetracker.core.network.status.NetworkConnectionMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(
        engine: HttpClientEngine,
    ): HttpClient {
        return HttpClientFactory.createHttpClient(
            engine = engine,
        )
    }

    @Provides
    @Singleton
    fun provideHttpClientExecutor(
        httpClient: HttpClient,
        networkMonitor: NetworkConnectionMonitor,
    ): HttpRequestExecutor {
        return HttpRequestExecutor(
            httpClient = httpClient,
            networkConnectionMonitor = networkMonitor,
        )
    }
}