package com.modeshift.routetracker.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUserNameStore @Inject constructor(
    @ApplicationContext
    private val appContext: Context
) {
    private val userKey = stringPreferencesKey("user_name")
    private val Context.userNameDataStore by preferencesDataStore(
        name = "user_name_datastore"
    )

    suspend fun storeUserName(name: String) {
        appContext.userNameDataStore.edit {
            it[userKey] = name
        }
    }

    suspend fun loadAppUserName(): String? {
        return appContext.userNameDataStore.data.map {
            it[userKey]
        }.firstOrNull()
    }

    suspend fun clear() {
        appContext.userNameDataStore.edit {
            it.remove(userKey)
        }
    }
}