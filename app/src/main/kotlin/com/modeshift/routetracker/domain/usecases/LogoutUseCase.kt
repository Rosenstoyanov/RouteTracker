package com.modeshift.routetracker.domain.usecases

import android.content.Context
import android.content.Intent
import com.modeshift.routetracker.data.store.ActiveRouteStore
import com.modeshift.routetracker.data.store.AppUserNameStore
import com.modeshift.routetracker.di.annotations.AppScope
import com.modeshift.routetracker.navigation.NavTarget.Login
import com.modeshift.routetracker.navigation.Navigator
import com.modeshift.routetracker.service.StopsTracingService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    @AppScope
    private val appScope: CoroutineScope,
    @ApplicationContext
    private val appContext: Context,
    private val navigator: Navigator,
    private val appUserNameStore: AppUserNameStore,
    private val activeRouteStore: ActiveRouteStore,
) {
    operator fun invoke() = appScope.launch {
        appUserNameStore.clear()
        activeRouteStore.clear()
        val intent = Intent(appContext, StopsTracingService::class.java)
        appContext.stopService(intent)
        navigator.navigate(Login)
    }
}