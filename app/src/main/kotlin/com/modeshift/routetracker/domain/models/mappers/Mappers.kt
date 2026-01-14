package com.modeshift.routetracker.domain.models.mappers

import com.modeshift.database.entity.VisitedStopEventEntity
import com.modeshift.models.EventType
import com.modeshift.models.Location
import com.modeshift.routetracker.data.network.dto.LocationDto
import com.modeshift.routetracker.data.network.dto.VisitedStopEventDto
import com.modeshift.routetracker.domain.models.Stop
import com.modeshift.routetracker.domain.models.VisitedStopEvent
import java.time.Instant

fun VisitedStopEvent.toEntity() = VisitedStopEventEntity(
    id = id,
    appUser = appUser,
    stopId = stopId,
    location = location,
    eventDateTime = eventDateTime,
    evenType = evenType
)

fun Location.toDto() = LocationDto(
    latitude = latitude,
    longitude = longitude
)

fun Location.toLocation() = android.location.Location("").apply {
    latitude = this@toLocation.latitude
    longitude = this@toLocation.longitude
}

fun VisitedStopEvent.toDto() = VisitedStopEventDto(
    appUser = appUser,
    stopId = stopId,
    location = location.toDto(),
    eventDateTime = eventDateTime,
    evenType = evenType
)

fun Stop.toVisitedStopEvent(
    appUser: String,
    evenType: EventType,
    now: Instant = Instant.now()
) = VisitedStopEvent(
    appUser = appUser,
    stopId = id,
    location = location,
    eventDateTime = now,
    evenType = evenType
)