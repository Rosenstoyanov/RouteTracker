package com.modeshift.routetracker.navigation.di

import com.modeshift.routetracker.navigation.Navigator
import com.modeshift.routetracker.navigation.NavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NavigatorModule {
    @Binds
    fun bindNavigator(navigatorImpl: NavigatorImpl): Navigator
}