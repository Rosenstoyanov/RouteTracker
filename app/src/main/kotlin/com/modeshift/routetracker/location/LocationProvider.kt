package com.modeshift.routetracker.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun getLocationUpdates(intervalMillis: Long): Flow<Location>
    suspend fun getCurrentLocation(): Location?
}