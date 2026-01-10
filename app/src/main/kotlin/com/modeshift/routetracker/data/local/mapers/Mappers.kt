package com.modeshift.routetracker.data.local.mapers

import com.modeshift.database.entity.RouteEntity
import com.modeshift.database.entity.StopEntity
import com.modeshift.routetracker.domain.models.Route
import com.modeshift.routetracker.domain.models.Stop

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