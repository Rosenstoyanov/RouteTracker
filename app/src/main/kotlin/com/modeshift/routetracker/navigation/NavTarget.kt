package com.modeshift.routetracker.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Serializable

sealed interface NavTarget {

    @Serializable
    data object Login : NavTarget

    @Serializable
    data object RouteSelection : NavTarget

    @Serializable
    data object RouteTracking : NavTarget

    companion object {
        fun withCleanStack(
            target: NavTarget? = null,
            targetInclusive: Boolean = true
        ): NavOptionsBuilder.() -> Unit {
            return {
                launchSingleTop = true
                target?.let {
                    popUpTo(target) {
                        inclusive = targetInclusive
                    }
                } ?: popUpTo(0)
            }
        }
    }
}