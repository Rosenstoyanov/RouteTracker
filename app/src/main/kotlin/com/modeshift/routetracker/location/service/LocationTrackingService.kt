package com.modeshift.routetracker.location.service

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.modeshift.routetracker.R
import com.modeshift.routetracker.di.CoroutinesDispatcherProvider
import com.modeshift.routetracker.domain.usecases.TrackLocationUseCase
import com.modeshift.routetracker.event_sync.StopEventsSyncExecutor
import com.modeshift.routetracker.event_sync.StopEventsSyncScheduler
import com.modeshift.routetracker.location.LocationProvider
import com.modeshift.routetracker.location.LocationServiceState.Running
import com.modeshift.routetracker.location.LocationServiceState.Stopped
import com.modeshift.routetracker.location.LocationServiceStateEmitter
import com.modeshift.routetracker.ui.MainActivity
import com.modeshift.routetracker.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class LocationTrackingService : LifecycleService() {
    @Inject
    lateinit var locationProvider: LocationProvider

    @Inject
    lateinit var trackLocationUseCase: TrackLocationUseCase

    @Inject
    lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    @Inject
    lateinit var locationServiceStateEmitter: LocationServiceStateEmitter

    @Inject
    lateinit var stopEventsSyncScheduler: StopEventsSyncScheduler

    @Inject
    lateinit var stopEventsSyncExecutor: StopEventsSyncExecutor

    @Volatile
    private var lastLocation: Location? = null
    private var isRunning: Boolean = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        setForeground()

        // TODO: If it is required check for mock location
        if (!isRunning) {
            isRunning = true
            locationServiceStateEmitter.updateState(Running)
            lifecycleScope.launch(dispatcherProvider.io) {
                locationProvider.getLocationUpdates(Constants.LOCATION_UPDATE_INTERVAL_IN_SECONDS.seconds.inWholeMilliseconds)
                    .buffer(capacity = Constants.LOCATION_UPDATE_BUFFER_CAPACITY)
                    .filter { currentLocation ->
                        lastLocation?.distanceTo(currentLocation)?.let {
                            it >= Constants.HAS_SIGNIFICANT_LOCATION_CHANGE_DELTA_IN_METERS
                        } ?: true
                    }
                    .catch { Timber.e(it) }
                    .collect { currentLocation ->
                        lastLocation = currentLocation
                        trackLocationUseCase(currentLocation)
                        stopEventsSyncExecutor.ensureRunning(this)
                    }
            }.invokeOnCompletion {
                locationServiceStateEmitter.updateState(Stopped)
                isRunning = false
                stopEventsSyncScheduler.scheduleOneTimeRequest()
            }
        }
        return START_STICKY
    }

    private fun setForeground() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification =
            NotificationCompat.Builder(this, Constants.DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.location_tracking_notification_title))
                .setContentText(getString(R.string.location_tracking_notification_content))
                .setSmallIcon(R.drawable.outline_directions_bus_24)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build()

        startForeground(1, notification, FOREGROUND_SERVICE_TYPE_LOCATION)
    }
}