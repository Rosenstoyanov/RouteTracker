package com.modeshift.routetracker.di

import com.modeshift.routetracker.di.annotations.AppScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule {

    @AppScope
    @Provides
    @Singleton
    fun provideApplicationCoroutineScope(dispatcherProvider: CoroutinesDispatcherProvider): CoroutineScope {
        return CoroutineScope(
            SupervisorJob() +
                    dispatcherProvider.mainImmediate +
                    CoroutineExceptionHandler { _, throwable ->
                        Timber.e(throwable)
                    }
        )
    }
}