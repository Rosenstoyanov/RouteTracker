package com.modeshift.routetracker.domain.usecases

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.modeshift.models.EventType
import com.modeshift.models.EventType.Right
import com.modeshift.models.EventType.Wrong
import com.modeshift.routetracker.R
import com.modeshift.routetracker.data.store.ActiveRouteStore
import com.modeshift.routetracker.data.store.AppUserNameStore
import com.modeshift.routetracker.domain.RouteTrackerRepository
import com.modeshift.routetracker.domain.models.mappers.toLocation
import com.modeshift.routetracker.domain.models.mappers.toVisitedStopEvent
import com.modeshift.routetracker.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Math.toRadians
import javax.inject.Inject
import kotlin.math.cos
import kotlin.time.ExperimentalTime

class TrackLocationUseCase @Inject constructor(
    private val repository: RouteTrackerRepository,
    private val appUserNameStore: AppUserNameStore,
    private val activeRouteStore: ActiveRouteStore,
    @ApplicationContext
    private val appContext: Context
) {
    private val notificationManager by lazy { NotificationManagerCompat.from(appContext) }

    // TODO: Check the business requirements if a bus is at location with multiple stops one right and several wrong
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(currentLocation: Location) {
        val latDelta = Constants.DELTA_RADIUS_TO_STOP_DB_LOOK_UP_IN_METERS / 111000.0
        val lngDelta = latDelta / cos(toRadians(currentLocation.latitude))

        val minLat = currentLocation.latitude - latDelta
        val maxLat = currentLocation.latitude + latDelta
        val minLng = currentLocation.longitude - lngDelta
        val maxLng = currentLocation.longitude + lngDelta

        val stops = repository.getStopsInArea(
            minLat = minLat,
            maxLat = maxLat,
            minLng = minLng,
            maxLng = maxLng
        ).filter { it.location.toLocation().distanceTo(currentLocation) <= it.radius }

        if (stops.isEmpty()) return
        val activeRouteId = activeRouteStore.routeId()
        val appUserId = appUserNameStore.loadAppUserName()
            ?: throw IllegalStateException("Missing app user name")

        val rightStops = stops.filter { it.routeId == activeRouteId }

        val stopsToRecord = if (rightStops.isNotEmpty()) {
            rightStops.map { it to Right }
        } else {
            stops.map { it to Wrong }
        }

        stopsToRecord.forEach { (stop, eventType) ->
            val event = stop.toVisitedStopEvent(
                appUser = appUserId,
                evenType = eventType
            )

            showStopVisitedNotification(stop.name, eventType)
            repository.trackVisitedStopEvent(event)
        }
    }

    private fun showStopVisitedNotification(stopName: String, eventType: EventType) {
        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val (title, icon) = when (eventType) {
                Right -> "Correct Stop!" to R.drawable.outline_check_circle_24
                Wrong -> "Wrong Stop!" to R.drawable.outline_warning_24
            }

            val notification =
                NotificationCompat.Builder(appContext, Constants.STOPS_REACHED_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText("You just reached: $stopName")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .build()

            notificationManager.notify(stopName.hashCode(), notification)
        }
    }
}