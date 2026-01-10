package com.modeshift.routetracker.data

import com.modeshift.routetracker.core.models.Resource
import com.modeshift.routetracker.core.models.Resource.Failure
import com.modeshift.routetracker.core.models.Resource.Success
import com.modeshift.routetracker.data.local.LocalDataSource
import com.modeshift.routetracker.data.local.mapers.toModel
import com.modeshift.routetracker.data.network.NetworkDataSource
import com.modeshift.routetracker.data.network.dto.VisitedStopEventDto
import com.modeshift.routetracker.data.network.mappers.toEntity
import com.modeshift.routetracker.domain.RouteTrackerRepository
import com.modeshift.routetracker.domain.models.Route
import com.modeshift.routetracker.domain.models.Stop
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
                Success(localDataSource.getAllRoutes().map { it.toModel() })
            }
        }
    }

    override suspend fun getStops(): Resource<List<Stop>> {
        return when (val result = networkDataSource.getStops()) {
            is Success -> {
                localDataSource.replaceAllStops(result.data.map { it.toEntity() })
                Success(localDataSource.getAllStops().map { it.toModel() })
            }

            is Failure -> {
                Success(localDataSource.getAllStops().map { it.toModel() })
            }
        }
    }

    override suspend fun sendVisitedStopEvent(visitedStopEvents: List<VisitedStopEventDto>): Resource<Unit> {
        TODO("Not yet implemented")
    }
}