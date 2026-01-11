package com.modeshift.routetracker.di

import com.modeshift.routetracker.data.RouteTrackerRepositoryImpl
import com.modeshift.routetracker.domain.RouteTrackerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RouteTrackerModule {

    @Binds
    @Singleton
    fun bindRouteTrackerRepository(routeTrackerRepository: RouteTrackerRepositoryImpl): RouteTrackerRepository
}