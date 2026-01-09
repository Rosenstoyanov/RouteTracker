package com.modeshift.routetracker.navigation

import kotlinx.coroutines.flow.SharedFlow

interface Navigator {
    val directions: SharedFlow<NavDirection>

    fun navigate(navTarget: NavTarget)

    fun goBack()
}