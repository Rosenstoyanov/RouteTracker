package com.modeshift.routetracker.core.location.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.modeshift.routetracker.core.location.LocationProvider
import com.modeshift.routetracker.core.location.LocationProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LocationModule {
    @Binds
    fun bindLocationProvider(locationProviderImpl: LocationProviderImpl): LocationProvider

    companion object {
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            @ApplicationContext appContext: Context
        ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)
    }
}