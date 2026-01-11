package com.modeshift.routetracker.ui.route_tracking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.modeshift.routetracker.R
import com.modeshift.routetracker.core.ui.components.RtAppBar
import com.modeshift.routetracker.core.ui.components.RtHostContent
import com.modeshift.routetracker.core.ui.utils.ObserveAsEvents
import com.modeshift.routetracker.core.ui.utils.RtPreview
import com.modeshift.routetracker.core.ui.utils.debounceClick
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.Logout
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.Refresh
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.SelectRoute
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.StartRoute
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.StopRoute
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingEvent
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingEvent.ShowMessage
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

    ObserveAsEvents(eventFlow) {
        when (it) {
            is ShowMessage -> coroutineScope.launch {
                snackBarHost.showSnackbar(it.message)
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
        floatingActionButton = {
            FilledIconButton(debounceClick { onAction(Refresh) }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_refresh_24),
                    contentDescription = null,
                )
            }
        },
        snackBarHostState = snackBarHost,
        showLoading = uiState.isLoading,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (uiState.selectedRoute != null) {
                Text(text = uiState.selectedRoute.name)
                Text(text = uiState.selectedRoute.description)
            } else {
                Text(text = stringResource(id = R.string.no_route_selected))
            }

            Button(debounceClick { onAction(SelectRoute) }) {
                Text(text = stringResource(id = R.string.select_route))
            }
            Button(debounceClick { onAction(StartRoute) }) {
                Text(text = "Start Route")
            }
            Button(debounceClick { onAction(StopRoute) }) {
                Text(text = "Stop Route")
            }
        }
    }
}

//@OptIn(MapsComposeExperimentalApi::class)
//@Composable
//private fun RouteTrackingContent(
//    uiState: RouteTrackingUiState,
//    onAction: (RouteTrackingAction) -> Unit
//) {
//    RtDimmedLoader(false) {
//        val snackBarHostState = remember { SnackbarHostState() }
//        Scaffold(
//            modifier = Modifier.fillMaxSize(),
//            topBar = {
//                RtRoadSelectionBar(
//                    routes = uiState.routes,
//                    onRouteSelected = { onAction(RouteSelected(it.id)) },
//                    onLogout = { onAction(Logout) }
//                )
//            },
//            snackbarHost = {
//                SnackbarHost(
//                    modifier = Modifier.imePadding(),
//                    hostState = snackBarHostState
//                )
//            }
//        ) {
//            RtMap(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(it),
//            ) {
//                Clustering(
//                    items = uiState.stopPins,
//                    clusterContent = { EmptyClusterContent() },
//                    clusterItemContent = { PinItem() }
//                )
//            }
//        }
//    }
//}

//@Composable
//private fun EmptyClusterContent() {
//    Box(modifier = Modifier.size(0.dp))
//}
//
//@Composable
//private fun PinItem() {
//    Icon(
//        modifier = Modifier
//            .background(color = Color.Red, shape = CircleShape)
//            .padding(2.dp),
//        painter = painterResource(id = R.drawable.outline_directions_bus_24),
//        contentDescription = null,
//        tint = Color.White
//    )
//}

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
