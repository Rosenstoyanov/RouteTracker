package com.modeshift.routetracker.domain

import com.modeshift.routetracker.core.models.Resource
import com.modeshift.routetracker.domain.models.Route
import com.modeshift.routetracker.domain.models.Stop
import com.modeshift.routetracker.domain.models.VisitedStopEvent

interface RouteTrackerRepository {
    suspend fun getRoutes(): Resource<List<Route>>
    suspend fun getRouteBy(id: Long): Route?
    suspend fun getStops(): Resource<List<Stop>>
    suspend fun sendVisitedStopEvents(visitedStopEvents: List<VisitedStopEvent>): Resource<Unit>
    suspend fun trackVisitedStopEvent(visitedStopEvents: VisitedStopEvent)
    suspend fun getStopsInArea(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<Stop>
}