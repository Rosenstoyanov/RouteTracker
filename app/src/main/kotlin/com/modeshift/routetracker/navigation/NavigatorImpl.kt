package com.modeshift.routetracker.navigation

import com.modeshift.routetracker.navigation.NavDirection.GoBack
import com.modeshift.routetracker.navigation.NavDirection.NavigateTo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigatorImpl @Inject constructor() : Navigator {
    private val _directions = MutableSharedFlow<NavDirection>(extraBufferCapacity = 1)
    override val directions = _directions.asSharedFlow()

    override fun navigate(navTarget: NavTarget) {
        _directions.tryEmit(NavigateTo(navTarget))
    }

    override fun goBack() {
        _directions.tryEmit(GoBack)
    }
}