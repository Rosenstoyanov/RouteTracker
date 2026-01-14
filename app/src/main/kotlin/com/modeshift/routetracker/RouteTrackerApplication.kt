package com.modeshift.routetracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.modeshift.routetracker.di.annotations.AppScope
import com.modeshift.routetracker.domain.AppDataInitializer
import com.modeshift.routetracker.event_sync.StopEventsSyncScheduler
import com.modeshift.routetracker.utils.Constants
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class RouteTrackerApplication : Application(), Configuration.Provider {

    @Inject
    @AppScope
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var appDataInitializer: AppDataInitializer

    @Inject
    lateinit var stopEventsSyncScheduler: StopEventsSyncScheduler

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()
        appDataInitializer.initialize()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        setupNotificationChannels()
        stopEventsSyncScheduler.scheduleDailyStopEventsUpload()
    }


    private fun setupNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val serviceChannel = NotificationChannel(
                Constants.DEFAULT_NOTIFICATION_CHANNEL_ID,
                getString(R.string.default_notification_chanel),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.default_channel_description)
            }

            manager.createNotificationChannel(serviceChannel)

            val stopsReachedChannel = NotificationChannel(
                Constants.STOPS_REACHED_NOTIFICATION_CHANNEL_ID,
                getString(R.string.stops_reached_notification_chanel),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.stops_reached_channel_description)
            }

            manager.createNotificationChannel(stopsReachedChannel)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appScope.cancel()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}