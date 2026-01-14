package com.modeshift.routetracker.data.local

import com.modeshift.database.dao.RoutesDao
import com.modeshift.database.dao.StopsDao
import com.modeshift.database.dao.VisitedStopEventsDao
import com.modeshift.database.entity.RouteEntity
import com.modeshift.database.entity.StopEntity
import com.modeshift.database.entity.VisitedStopEventEntity
import com.modeshift.routetracker.di.annotations.AppScope
import com.modeshift.routetracker.domain.models.VisitedStopEvent
import com.modeshift.routetracker.domain.models.mappers.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val routesDao: RoutesDao,
    private val stopsDao: StopsDao,
    private val visitedStopEventsDao: VisitedStopEventsDao,
    @AppScope
    private val appScope: CoroutineScope
) {
    suspend fun getAllRoutes(): List<RouteEntity> {
        return routesDao.getAllRoutes()
    }

    suspend fun getRouteBy(id: Long): RouteEntity? {
        return routesDao.getRouteBy(id)
    }

    suspend fun getAllStops(): List<StopEntity> {
        return stopsDao.getAllStops()
    }

    suspend fun replaceAllRoutes(routes: List<RouteEntity>) = executeInAppScope {
        try {
            routesDao.replaceRoutes(routes)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    suspend fun replaceAllStops(stops: List<StopEntity>) = executeInAppScope {
        stopsDao.replaceAllStops(stops)
    }

    suspend fun getStopsInArea(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<StopEntity> {
        return stopsDao.getStopsInArea(minLat, maxLat, minLng, maxLng)
    }

    suspend fun trackVisitedStopEvent(visitedStopEvent: VisitedStopEventEntity) = executeInAppScope {
        visitedStopEventsDao.insert(visitedStopEvent)
    }

    suspend fun deleteVisitedStopEvents(visitedStopEvents: List<VisitedStopEventEntity>) =
        executeInAppScope {
            visitedStopEventsDao.delete(visitedStopEvents)
        }

    suspend fun getVisitedStopEvents(): List<VisitedStopEventEntity> {
        return visitedStopEventsDao.getAll()
    }

    fun visitedStopEventsFlow(limit: Int): Flow<List<VisitedStopEventEntity>> {
        return visitedStopEventsDao.visitedStopEventsFlow(limit)
    }

    suspend fun stopEventsSafeIncrementFailureCount(ids: List<Long>) = executeInAppScope {
        visitedStopEventsDao.safeIncrementFailureCount(ids)
    }

    private suspend fun <Result> executeInAppScope(block: suspend () -> Result) =
        appScope.async { block() }.await()
}