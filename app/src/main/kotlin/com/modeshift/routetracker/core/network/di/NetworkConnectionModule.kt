package com.modeshift.routetracker.core.network.di

import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkRequest
import com.modeshift.routetracker.core.network.status.NetworkConnectionMonitor
import com.modeshift.routetracker.core.network.status.NetworkConnectionMonitorImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkConnectionModule {
    @Singleton
    @Provides
    fun provideNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(TRANSPORT_WIFI)
            .addTransportType(TRANSPORT_ETHERNET)
            .addTransportType(TRANSPORT_CELLULAR)
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface NetworkConnectionBindingModule {
    @Binds
    fun bindNetworkConnectionMonitor(networkConnectionMonitor: NetworkConnectionMonitorImpl): NetworkConnectionMonitor
}