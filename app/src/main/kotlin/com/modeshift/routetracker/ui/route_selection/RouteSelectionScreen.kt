package com.modeshift.routetracker.ui.route_selection

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.modeshift.routetracker.R
import com.modeshift.routetracker.core.ui.components.RtAppBar
import com.modeshift.routetracker.core.ui.components.RtHostContent
import com.modeshift.routetracker.core.ui.utils.ObserveAsEvents
import com.modeshift.routetracker.core.ui.utils.RtPreview
import com.modeshift.routetracker.core.ui.utils.debounceClickable
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionAction
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionAction.GoBack
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionAction.Refresh
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionAction.RoadSelected
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionEvent
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionEvent.ShowMessage
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun RouteSelectionScreen(
    viewModel: RouteSelectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RouteSelectionContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        eventFlow = viewModel.events
    )
}

@Composable
private fun RouteSelectionContent(
    uiState: RouteSelectionUiState,
    onAction: (RouteSelectionAction) -> Unit,
    eventFlow: Flow<RouteSelectionEvent>
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
                titleResId = R.string.select_route,
                goBack = { onAction(GoBack) },
            )
        },
        showLoading = uiState.isLoading,
        snackBarHostState = snackBarHost
    ) {
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { onAction(Refresh) }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                items(
                    items = uiState.routes,
                    key = { it.id }
                ) { route ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .debounceClickable {
                                onAction(RoadSelected(route.id))
                            }
                            .padding(16.dp),
                        text = route.toLabel(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RouteSelectionScreenPreview() {
    RtPreview {
        RouteSelectionContent(
            uiState = RouteSelectionUiState(),
            onAction = {},
            eventFlow = flowOf()
        )
    }
}