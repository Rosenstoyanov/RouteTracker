package com.modeshift.routetracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.compose.ui.unit.Constraints
import com.modeshift.routetracker.di.annotations.AppScope
import com.modeshift.routetracker.domain.AppDataInitializer
import com.modeshift.routetracker.utils.Constants
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

    @Inject
    lateinit var appDataInitializer: AppDataInitializer


    override fun onCreate() {
        super.onCreate()
        appDataInitializer.initialize()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        setupNotificationChannel()
    }


    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                Constants.DEFAULT_NOTIFICATION_CHANNEL_ID,
                getString(R.string.default_notification_chanel),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.default_channel_description)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appScope.cancel()
    }
}