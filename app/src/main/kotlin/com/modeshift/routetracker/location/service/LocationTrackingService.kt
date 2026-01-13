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
import com.modeshift.routetracker.location.LocationProvider
import com.modeshift.routetracker.location.LocationServiceState.Running
import com.modeshift.routetracker.location.LocationServiceState.Stopped
import com.modeshift.routetracker.location.LocationServiceStateEmitter
import com.modeshift.routetracker.ui.MainActivity
import com.modeshift.routetracker.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

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

    @Volatile
    private var lastLocation: Location? = null
    private var isRunning: Boolean = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        setForeground()

        // TODO: Can extract deltaRadiusInMeters and delta distance for significantChange in constants
        // TODO: if it is required check for mock location
        if (!isRunning) {
            isRunning = true
            locationServiceStateEmitter.updateState(Running)
            lifecycleScope.launch(dispatcherProvider.io) {
                locationProvider.getLocationUpdates(1000)
                    .buffer(capacity = 100)
                    .catch { Timber.e(it) }
                    .collect { currentLocation ->
                        val distanceToPreviousKnownLocation = lastLocation
                            ?.distanceTo(currentLocation) ?: 0f
                        val hasSignificantChange = distanceToPreviousKnownLocation >= 200f

                        if (hasSignificantChange) {
                            lastLocation = currentLocation
                            trackLocationUseCase(currentLocation)
                        }
                    }
            }.invokeOnCompletion {
                locationServiceStateEmitter.updateState(Stopped)
                isRunning = false
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