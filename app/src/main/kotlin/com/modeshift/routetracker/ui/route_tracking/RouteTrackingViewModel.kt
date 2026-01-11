package com.modeshift.routetracker.ui.route_tracking

import androidx.lifecycle.viewModelScope
import com.modeshift.routetracker.core.BaseViewModel
import com.modeshift.routetracker.core.extensions.toStopPin
import com.modeshift.routetracker.core.models.onFailure
import com.modeshift.routetracker.core.models.onSuccess
import com.modeshift.routetracker.core.ui.models.StopPin
import com.modeshift.routetracker.domain.RouteTrackerRepository
import com.modeshift.routetracker.domain.models.Route
import com.modeshift.routetracker.domain.usecases.LogoutUseCase
import com.modeshift.routetracker.navigation.NavTarget.RouteSelection
import com.modeshift.routetracker.navigation.Navigator
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.Logout
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.Refresh
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.SelectRoute
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.StartRoute
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction.StopRoute
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteTrackingViewModel @Inject constructor(
    private val navigator: Navigator,
    private val routeTrackerRepository: RouteTrackerRepository,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<RouteTrackingUiState, RouteTrackingAction>(RouteTrackingUiState()) {

    private val _events = Channel<RouteTrackingEvent>()
    val events = _events.receiveAsFlow()
    
    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            coroutineScope {
                launch {
                    routeTrackerRepository.getStops()
                        .onSuccess { result ->
                            updateState { it.copy(stopPins = result.data.map { it.toStopPin() }) }
                        }.onFailure { error ->
                            updateState { it.copy(error = error.message) }
                        }
                }
                launch {
                    routeTrackerRepository.getRoutes()
                        .onSuccess { result ->
                            updateState { it.copy(routes = result.data) }
                        }.onFailure { error ->
                            updateState { it.copy(error = error.message) }
                        }
                }
            }
            updateState { it.copy(isLoading = false) }
        }
    }

    override fun onAction(action: RouteTrackingAction) {
        when (action) {
            Logout -> logoutUseCase()
            Refresh -> loadData()
            SelectRoute -> navigator.navigate(RouteSelection)
            StartRoute -> TODO()
            StopRoute -> TODO()
        }
    }

    data class RouteTrackingUiState(
        val routes: List<Route> = emptyList(),
        val stopPins: List<StopPin> = emptyList(),
        val selectedRoute: Route? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    sealed interface RouteTrackingAction {
        data object Logout : RouteTrackingAction
        data object Refresh : RouteTrackingAction
        data object SelectRoute : RouteTrackingAction
        data object StartRoute : RouteTrackingAction
        data object StopRoute : RouteTrackingAction
    }

    sealed interface RouteTrackingEvent {
        data class ShowMessage(val message: String) : RouteTrackingEvent
    }
}