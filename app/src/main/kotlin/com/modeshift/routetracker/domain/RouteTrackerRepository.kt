package com.modeshift.routetracker.domain

import com.modeshift.routetracker.core.models.Resource
import com.modeshift.routetracker.data.network.dto.VisitedStopEventDto
import com.modeshift.routetracker.domain.models.Route
import com.modeshift.routetracker.domain.models.Stop

interface RouteTrackerRepository {
    suspend fun getRoutes(): Resource<List<Route>>
    suspend fun getStops(): Resource<List<Stop>>
    suspend fun sendVisitedStopEvent(visitedStopEvents: List<VisitedStopEventDto>): Resource<Unit>
}