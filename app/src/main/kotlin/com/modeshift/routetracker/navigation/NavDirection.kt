package com.modeshift.routetracker.navigation

import androidx.navigation.NavOptionsBuilder

interface NavDirection {
    data class NavigateTo(
        val target: NavTarget,
        val builder: NavOptionsBuilder.() -> Unit
    ) : NavDirection

    data object GoBack : NavDirection
}
