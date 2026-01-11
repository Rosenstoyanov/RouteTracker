package com.modeshift.routetracker.ui.route_selection

import androidx.lifecycle.viewModelScope
import com.modeshift.routetracker.core.BaseViewModel
import com.modeshift.routetracker.core.models.onFailure
import com.modeshift.routetracker.core.models.onSuccess
import com.modeshift.routetracker.data.store.ActiveRouteStore
import com.modeshift.routetracker.domain.RouteTrackerRepository
import com.modeshift.routetracker.domain.models.Route
import com.modeshift.routetracker.navigation.Navigator
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionAction
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionAction.GoBack
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionAction.Refresh
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionAction.RoadSelected
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionEvent.ShowMessage
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteSelectionViewModel @Inject constructor(
    private val navigator: Navigator,
    private val routeTrackerRepository: RouteTrackerRepository,
    private val activeRoadStore: ActiveRouteStore
) : BaseViewModel<RouteSelectionUiState, RouteSelectionAction>(RouteSelectionUiState()) {

    private val _events = Channel<RouteSelectionEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    override fun onAction(action: RouteSelectionAction) {
        when (action) {
            GoBack -> navigator.goBack()
            Refresh -> loadData()
            is RoadSelected -> viewModelScope.launch {
                activeRoadStore.storeRouteId(action.roadId)
                navigator.goBack()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            routeTrackerRepository.getRoutes()
                .onSuccess { result ->
                    updateState { it.copy(routes = result.data) }
                }.onFailure {
                    _events.trySend(ShowMessage(it.message))
                }
        }
    }

    data class RouteSelectionUiState(
        val routes: List<Route> = emptyList(),
        val isLoading: Boolean = false,
    )

    sealed interface RouteSelectionAction {
        data object GoBack : RouteSelectionAction
        data object Refresh : RouteSelectionAction
        data class RoadSelected(val roadId: Long) : RouteSelectionAction
    }

    sealed interface RouteSelectionEvent {
        data class ShowMessage(val message: String) : RouteSelectionEvent
    }
}