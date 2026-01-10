package com.modeshift.routetracker.data.local

import com.modeshift.database.dao.RoutesDao
import com.modeshift.database.dao.StopsDao
import com.modeshift.database.entity.RouteEntity
import com.modeshift.database.entity.StopEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val routesDao: RoutesDao,
    private val stopsDao: StopsDao
) {
    suspend fun getAllRoutes() = routesDao.getAllRoutes()

    suspend fun getAllStops() = stopsDao.getAllStops()

    suspend fun replaceAllRoutes(routes: List<RouteEntity>) {
        try {
            routesDao.replaceRoutes(routes)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    suspend fun replaceAllStops(stops: List<StopEntity>) = stopsDao.replaceAllStops(stops)

}