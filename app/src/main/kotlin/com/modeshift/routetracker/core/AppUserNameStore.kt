package com.modeshift.routetracker.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUserNameStore @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val userKey = stringPreferencesKey("user_name")
    private val Context.userNameDataStore by preferencesDataStore(
        name = "user_name_datastore"
    )

    suspend fun storeUserName(name: String) {
        context.userNameDataStore.edit {
            it[userKey] = name
        }
    }

    suspend fun loadAppUserName(): String? {
        return context.userNameDataStore.data.map {
            it[userKey]
        }.first()
    }

    suspend fun clear() {
        context.userNameDataStore.edit {
            it.remove(userKey)
        }
    }
}