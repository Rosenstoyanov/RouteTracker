package com.modeshift.routetracker.domain.usecases

import com.modeshift.routetracker.data.store.ActiveRouteStore
import com.modeshift.routetracker.data.store.AppUserNameStore
import com.modeshift.routetracker.di.annotations.AppScope
import com.modeshift.routetracker.navigation.NavTarget.Login
import com.modeshift.routetracker.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    @AppScope
    private val appScope: CoroutineScope,
    private val navigator: Navigator,
    private val appUserNameStore: AppUserNameStore,
    private val activeRouteStore: ActiveRouteStore,
) {
    operator fun invoke() = appScope.launch {
        appUserNameStore.clear()
        activeRouteStore.clear()
        navigator.navigate(Login)
    }
}