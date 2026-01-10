package com.modeshift.routetracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.modeshift.routetracker.navigation.LoginDestination.login
import com.modeshift.routetracker.navigation.RouteSelectionDestination.routeSelection
import com.modeshift.routetracker.navigation.RouteTrackingDestination.routeTracking
import timber.log.Timber

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: NavTarget,
    modifier: Modifier = Modifier,
) {
    DisposableEffect(navController) {
        val destinationChangedListener =
            NavController.OnDestinationChangedListener { _, destination, arguments ->
                Timber.i("Nav Destination changed to: ${destination.route} with arguments: $arguments")
            }
        navController.addOnDestinationChangedListener(destinationChangedListener)

        onDispose {
            navController.removeOnDestinationChangedListener(destinationChangedListener)
        }
    }

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination,
    ) {
        login()
        routeSelection()
        routeTracking()
    }
}