package com.modeshift.routetracker.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveRouteStore @Inject constructor(
    @ApplicationContext
    private val appContext: Context
) {
    private val routeIdKey = longPreferencesKey("active_route_id")
    private val Context.activeRouteIdDataStore by preferencesDataStore(
        name = "active_route_id_datastore"
    )

    suspend fun storeRouteId(routeId: Long) {
        appContext.activeRouteIdDataStore.edit {
            it[routeIdKey] = routeId
        }
    }

    suspend fun routeId(): Long? {
        return appContext.activeRouteIdDataStore.data.map {
            it[routeIdKey]
        }.firstOrNull()
    }

    fun routeIdFlow(): Flow<Long> {
        return appContext.activeRouteIdDataStore.data.map {
            it[routeIdKey]
        }.filterNotNull()
    }

    suspend fun clear() {
        appContext.activeRouteIdDataStore.edit {
            it.remove(routeIdKey)
        }
    }
}