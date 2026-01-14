package com.modeshift.routetracker.core.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun getLocationUpdates(intervalMillis: Long): Flow<Location>
}