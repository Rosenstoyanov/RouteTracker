package com.modeshift.routetracker.data.network.mappers

import com.modeshift.database.entity.RouteEntity
import com.modeshift.database.entity.StopEntity
import com.modeshift.models.Location
import com.modeshift.routetracker.data.network.dto.RouteDto
import com.modeshift.routetracker.data.network.dto.StopDto

fun RouteDto.toEntity() = RouteEntity(
    id = id,
    name = name,
    description = description
)

fun StopDto.toEntity() = StopEntity(
    id = id,
    name = name,
    routeId = routeId,
    location = Location(latitude, longitude),
    radius = radius
)