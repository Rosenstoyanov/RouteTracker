package com.modeshift.routetracker.core.network.di

import com.modeshift.routetracker.core.network.httpclient.HttpClientFactory.createDefaultEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.engine.HttpClientEngine
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpClientEngineModule {
    @Provides
    @Singleton
    fun provideHttpClientEngine(): HttpClientEngine = createDefaultEngine()
}