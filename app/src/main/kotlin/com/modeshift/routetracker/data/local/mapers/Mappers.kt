package com.modeshift.routetracker.data.local.mapers

import com.modeshift.database.entity.RouteEntity
import com.modeshift.database.entity.StopEntity
import com.modeshift.database.entity.VisitedStopEventEntity
import com.modeshift.routetracker.domain.models.Route
import com.modeshift.routetracker.domain.models.Stop
import com.modeshift.routetracker.domain.models.VisitedStopEvent

fun RouteEntity.toModel() = Route(
    id = id,
    name = name,
    description = description
)

fun StopEntity.toModel() = Stop(
    id = id,
    name = name,
    routeId = routeId,
    location = location,
    radius = radius
)

fun VisitedStopEventEntity.toModel() = VisitedStopEvent(
    id = id,
    appUser = appUser,
    stopId = stopId,
    location = location,
    eventDateTime = eventDateTime,
    evenType = evenType
)