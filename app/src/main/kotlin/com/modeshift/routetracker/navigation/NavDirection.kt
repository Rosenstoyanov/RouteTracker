package com.modeshift.routetracker.navigation

sealed interface NavDirection {
    data class NavigateTo(val target: NavTarget) : NavDirection
    data object GoBack : NavDirection
}
