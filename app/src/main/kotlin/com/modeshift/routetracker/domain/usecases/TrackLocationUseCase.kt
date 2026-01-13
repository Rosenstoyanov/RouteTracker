package com.modeshift.routetracker.domain.usecases

import android.location.Location
import com.modeshift.models.EventType
import com.modeshift.routetracker.core.extensions.toLocation
import com.modeshift.routetracker.data.store.ActiveRouteStore
import com.modeshift.routetracker.data.store.AppUserNameStore
import com.modeshift.routetracker.domain.RouteTrackerRepository
import com.modeshift.routetracker.domain.models.mappers.toVisitedStopEvent
import com.modeshift.routetracker.utils.Constants
import java.lang.Math.toRadians
import javax.inject.Inject
import kotlin.math.cos
import kotlin.time.ExperimentalTime

class TrackLocationUseCase @Inject constructor(
    private val repository: RouteTrackerRepository,
    private val appUserNameStore: AppUserNameStore,
    private val activeRouteStore: ActiveRouteStore,
) {
    // TODO: Check the business requirements if a bus is at location with multiple stops one right and several wrong
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(currentLocation: Location) {
        val latDelta = Constants.DELTA_RADIUS_TO_STOP_DB_LOOK_UP_IN_METERS / 111000.0
        val lngDelta = latDelta / cos(toRadians(currentLocation.latitude))

        val minLat = currentLocation.latitude - latDelta
        val maxLat = currentLocation.latitude + latDelta
        val minLng = currentLocation.longitude - lngDelta
        val maxLng = currentLocation.longitude + lngDelta

        repository.getStopsInArea(
            minLat = minLat,
            maxLat = maxLat,
            minLng = minLng,
            maxLng = maxLng
        ).filter { it.location.toLocation().distanceTo(currentLocation) <= it.radius }
            .forEach {
                val appUserId = appUserNameStore.loadAppUserName()
                    ?: throw IllegalStateException("Mising app user name")
                val evenType = if (it.routeId == activeRouteStore.routeId()) {
                    EventType.Right
                } else {
                    EventType.Wrong
                }
                repository.trackVisitedStopEvent(
                    it.toVisitedStopEvent(
                        appUser = appUserId,
                        evenType = evenType
                    )
                )
            }
    }
}