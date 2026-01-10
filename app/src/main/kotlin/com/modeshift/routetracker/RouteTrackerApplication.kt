package com.modeshift.routetracker

import android.app.Application
import com.modeshift.routetracker.di.annotations.AppScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class RouteTrackerApplication : Application() {

    @Inject
    @AppScope
    lateinit var appScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appScope.cancel()
    }
}