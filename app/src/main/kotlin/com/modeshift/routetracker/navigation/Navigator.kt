package com.modeshift.routetracker.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.SharedFlow

interface Navigator {
    val directions: SharedFlow<NavDirection>

    fun navigate(navTarget: NavTarget, builder: NavOptionsBuilder.() -> Unit = {})

    fun goBack()
}