package com.modeshift.routetracker.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveRouteStore @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val routeIdKey = stringPreferencesKey("active_route_id")
    private val Context.activeRouteIdDataStore by preferencesDataStore(
        name = "active_route_id_datastore"
    )

    suspend fun storeRouteId(routeId: String) {
        context.activeRouteIdDataStore.edit {
            it[routeIdKey] = routeId
        }
    }

    fun routeIdFlow(): Flow<String> {
        return context.activeRouteIdDataStore.data.map {
            it[routeIdKey]
        }.filterNotNull()
    }

    suspend fun clear() {
        context.activeRouteIdDataStore.edit {
            it.remove(routeIdKey)
        }
    }
}