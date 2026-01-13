package com.modeshift.routetracker.ui.route_tracking

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.modeshift.routetracker.R
import com.modeshift.routetracker.core.extensions.hasLocationPermission
import com.modeshift.routetracker.core.extensions.isLocationEnabled
import com.modeshift.routetracker.core.ui.components.RtAppBar
import com.modeshift.routetracker.core.ui.components.RtHostContent
import com.modeshift.routetracker.core.ui.components.location.LocationAvailabilityDetector
import com.modeshift.routetracker.core.ui.components.location.LocationNotAvailable
import com.modeshift.routetracker.core.ui.utils.ObserveAsEvents
import com.modeshift.routetracker.core.ui.utils.RtPreview
import com.modeshift.routetracker.core.ui.utils.RtResumeDetector
import com.modeshift.routetracker.core.ui.utils.debounceClick
import com.modeshift.routetracker.domain.InitializationSate
import com.modeshift.routetracker.location.LocationServiceState.Running
import com.modeshift.routetracker.location.LocationServiceState.Stopped
import com.modeshift.routetracker.location.service.LocationTrackingService
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.Logout
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.Refresh
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.SelectRoute
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.StartRoute
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.StopRoute
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingEvent
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingEvent.ShowMessage
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingEvent.StartService
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingEvent.StopService
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun RouteTrackingScreen(
    viewModel: RouteTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RouteTrackingContent(
        uiState,
        viewModel::onAction,
        viewModel.events
    )
}


@OptIn(MapsComposeExperimentalApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun RouteTrackingContent(
    uiState: RouteTrackingUiState,
    onAction: (RouteTrackingAction) -> Unit,
    eventFlow: Flow<RouteTrackingEvent>,
) {
    val snackBarHost = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showLocationPermissionNotGranted by remember { mutableStateOf(!context.hasLocationPermission()) }
    var showLocationNotEnabled by remember { mutableStateOf(!context.isLocationEnabled()) }

    RtResumeDetector {
        showLocationPermissionNotGranted = !context.hasLocationPermission()
        showLocationNotEnabled = !context.isLocationEnabled()
    }
    LocationAvailabilityDetector { showLocationNotEnabled = !it }

    ObserveAsEvents(eventFlow) {
        when (it) {
            is ShowMessage -> coroutineScope.launch {
                snackBarHost.showSnackbar(it.message)
            }

            StartService -> {
                val intent = Intent(context, LocationTrackingService::class.java)
                ContextCompat.startForegroundService(context, intent)
            }

            StopService -> {
                val intent = Intent(context, LocationTrackingService::class.java)
                context.stopService(intent)
            }
        }
    }


    RtHostContent(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            RtAppBar(
                titleResId = R.string.tracking,
                actions = {
                    IconButton(debounceClick { onAction(Logout) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_logout_24),
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        snackBarHostState = snackBarHost,
        showLoading = uiState.isLoading,
    ) {
        if (showLocationPermissionNotGranted || showLocationNotEnabled) {
            LocationNotAvailable(
                modifier = Modifier.padding(it),
                showLocationPermissionNotGranted = showLocationPermissionNotGranted,
                showLocationNotEnabled = showLocationNotEnabled,
                onLocationEnabled = {
                    showLocationNotEnabled = false
                    showLocationPermissionNotGranted = false
                    onAction(Refresh)
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when {
                    uiState.appInitializedState != InitializationSate.Initialized -> {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = stringResource(R.string.app_not_initialized),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Button(debounceClick { onAction(Refresh) }) {
                            Text(text = stringResource(R.string.initialize))
                        }
                    }

                    uiState.selectedRoute != null -> {
                        Text(
                            text = uiState.selectedRoute.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = uiState.selectedRoute.description,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.padding(2.dp))

                        when (uiState.locationServiceState) {
                            Running -> Button(debounceClick { onAction(StopRoute) }) {
                                Text(text = stringResource(R.string.stop_route))
                            }

                            Stopped -> {
                                Button(debounceClick { onAction(StartRoute) }) {
                                    Text(text = stringResource(R.string.start_route))
                                }
                                Spacer(modifier = Modifier.padding(2.dp))
                                Button(debounceClick { onAction(SelectRoute) }) {
                                    Text(text = stringResource(id = R.string.select_route))
                                }
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = stringResource(id = R.string.no_route_selected),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        Button(debounceClick { onAction(SelectRoute) }) {
                            Text(text = stringResource(id = R.string.select_route))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RouteTrackingScreenPreview() {
    RtPreview {
        RouteTrackingContent(
            uiState = RouteTrackingUiState(),
            onAction = {},
            eventFlow = flowOf()
        )
    }
}
