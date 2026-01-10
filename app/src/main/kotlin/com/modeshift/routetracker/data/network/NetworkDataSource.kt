package com.modeshift.routetracker.data.network

import com.modeshift.routetracker.core.models.Resource
import com.modeshift.routetracker.data.network.dto.RouteDto
import com.modeshift.routetracker.data.network.dto.StopDto
import com.modeshift.routetracker.data.network.dto.VisitedStopEventDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDataSource @Inject constructor(
    private val routeTrackerApiService: RouteTrackerApiService
) {
    suspend fun getRoutes(): Resource<List<RouteDto>> {
        return routeTrackerApiService.getRoutes()
    }

    suspend fun getStops(): Resource<List<StopDto>> {
        return routeTrackerApiService.getStops()
    }

    suspend fun sendVisitedStopEvent(visitedStopEvents: List<VisitedStopEventDto>): Resource<Unit> {
        return routeTrackerApiService.sendVisitedStopEvent(visitedStopEvents)
    }
}