package com.modeshift.routetracker.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Serializable

sealed interface NavTarget {

    @Serializable
    object Login : NavTarget

    @Serializable
    object RouteSelection : NavTarget

    @Serializable
    object RouteTracking : NavTarget

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