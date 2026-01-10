package com.modeshift.routetracker.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.modeshift.routetracker.ui.login.LoginScreen
import com.modeshift.routetracker.ui.route_selection.RouteSelectionScreen
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingScreen

internal sealed interface Destination

data object LoginDestination : Destination {
    fun NavGraphBuilder.login() {
        composable<NavTarget.Login> {
            LoginScreen()
        }
    }
}

data object RouteTrackingDestination : Destination {
    fun NavGraphBuilder.routeTracking() {
        composable<NavTarget.RouteTracking> {
            RouteTrackingScreen()
        }
    }
}

data object RouteSelectionDestination : Destination {
    fun NavGraphBuilder.routeSelection() {
        composable<NavTarget.RouteSelection> {
            RouteSelectionScreen()
        }
    }
}

