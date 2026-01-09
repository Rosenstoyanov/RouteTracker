package com.modeshift.routetracker.navigation

import kotlinx.serialization.Serializable

interface NavTarget {
    @Serializable
    object RouteSelection : NavTarget

    @Serializable
    object RouteTracking : NavTarget
}