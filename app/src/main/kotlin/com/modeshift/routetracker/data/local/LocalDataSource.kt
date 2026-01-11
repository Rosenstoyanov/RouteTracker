package com.modeshift.routetracker.data.local

import com.modeshift.database.dao.RoutesDao
import com.modeshift.database.dao.StopsDao
import com.modeshift.database.entity.RouteEntity
import com.modeshift.database.entity.StopEntity
import com.modeshift.routetracker.di.annotations.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val routesDao: RoutesDao,
    private val stopsDao: StopsDao,
    @AppScope
    private val appScope: CoroutineScope
) {
    suspend fun getAllRoutes() = executeInAppScope { routesDao.getAllRoutes() }

    suspend fun getAllStops() = executeInAppScope { stopsDao.getAllStops() }

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

    private suspend fun <Result> executeInAppScope(block: suspend () -> Result) =
        appScope.async { block() }.await()

}