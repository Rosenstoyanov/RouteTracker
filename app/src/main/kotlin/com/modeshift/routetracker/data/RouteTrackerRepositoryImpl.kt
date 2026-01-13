package com.modeshift.routetracker.data

import com.modeshift.routetracker.core.models.Resource
import com.modeshift.routetracker.core.models.Resource.Failure
import com.modeshift.routetracker.core.models.Resource.Success
import com.modeshift.routetracker.core.models.onSuccess
import com.modeshift.routetracker.data.local.LocalDataSource
import com.modeshift.routetracker.data.local.mapers.toModel
import com.modeshift.routetracker.data.network.NetworkDataSource
import com.modeshift.routetracker.data.network.mappers.toEntity
import com.modeshift.routetracker.domain.RouteTrackerRepository
import com.modeshift.routetracker.domain.models.Route
import com.modeshift.routetracker.domain.models.Stop
import com.modeshift.routetracker.domain.models.VisitedStopEvent
import com.modeshift.routetracker.domain.models.mappers.toDto
import com.modeshift.routetracker.domain.models.mappers.toEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteTrackerRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource
) : RouteTrackerRepository {
    override suspend fun getRoutes(): Resource<List<Route>> {
        return when (val result = networkDataSource.getRoutes()) {
            is Success -> {
                localDataSource.replaceAllRoutes(result.data.map { it.toEntity() })
                Success(localDataSource.getAllRoutes().map { it.toModel() })
            }

            is Failure -> {
                val routes = localDataSource.getAllRoutes().map { it.toModel() }
                if (routes.isNotEmpty()) {
                    Success(routes)
                } else {
                    Failure(result.message)
                }
            }
        }
    }

    override suspend fun getRouteBy(id: Long): Route? {
        return localDataSource.getRouteBy(id)?.toModel()
    }

    override suspend fun getStops(): Resource<List<Stop>> {
        return when (val result = networkDataSource.getStops()) {
            is Success -> {
                localDataSource.replaceAllStops(result.data.map { it.toEntity() })
                Success(localDataSource.getAllStops().map { it.toModel() })
            }

            is Failure -> {
                val stops = localDataSource.getAllStops().map { it.toModel() }
                if (stops.isNotEmpty()) {
                    Success(stops)
                } else {
                    Failure(result.message)
                }
            }
        }
    }

    override suspend fun sendVisitedStopEvents(visitedStopEvents: List<VisitedStopEvent>): Resource<Unit> {
        return networkDataSource.sendVisitedStopEvent(visitedStopEvents.map { it.toDto() })
            .onSuccess {
                localDataSource.deleteVisitedStopEvents(visitedStopEvents.map { it.toEntity() })
            }
    }

    override suspend fun trackVisitedStopEvent(visitedStopEvents: VisitedStopEvent) {
        localDataSource.trackVisitedStopEvent(visitedStopEvents)
    }

    override suspend fun getStopsInArea(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<Stop> {
        return localDataSource.getStopsInArea(minLat, maxLat, minLng, maxLng).map { it.toModel() }
    }
}