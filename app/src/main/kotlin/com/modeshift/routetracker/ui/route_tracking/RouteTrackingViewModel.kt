package com.modeshift.routetracker.ui.route_tracking

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.modeshift.routetracker.core.BaseViewModel
import com.modeshift.routetracker.core.location.LocationServiceState
import com.modeshift.routetracker.core.location.LocationServiceStateEmitter
import com.modeshift.routetracker.data.store.ActiveRouteStore
import com.modeshift.routetracker.di.CoroutinesDispatcherProvider
import com.modeshift.routetracker.domain.AppDataInitializer
import com.modeshift.routetracker.domain.InitializationSate
import com.modeshift.routetracker.domain.InitializationSate.Error
import com.modeshift.routetracker.domain.InitializationSate.Idle
import com.modeshift.routetracker.domain.InitializationSate.Initialized
import com.modeshift.routetracker.domain.InitializationSate.Loading
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
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingEvent.ShowMessage
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingEvent.StartTracking
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingEvent.StopTracking
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteTrackingViewModel @Inject constructor(
    private val navigator: Navigator,
    private val repository: RouteTrackerRepository,
    private val logoutUseCase: LogoutUseCase,
    private val activeRouteStore: ActiveRouteStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val appDataInitializer: AppDataInitializer,
    private val locationServiceStateEmitter: LocationServiceStateEmitter,
) : BaseViewModel<RouteTrackingUiState, RouteTrackingAction>(RouteTrackingUiState()) {

    private val _events = Channel<RouteTrackingEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            launch(dispatcherProvider.io) {
                appDataInitializer.state.collectLatest { state ->
                    when (state) {
                        is Error -> {
                            _events.send(ShowMessage(state.message))
                            updateState {
                                it.copy(
                                    appInitializedState = state,
                                    isLoading = false
                                )
                            }
                        }

                        Initialized -> updateState {
                            it.copy(
                                appInitializedState = state,
                                isLoading = false,
                            )
                        }

                        Loading -> updateState {
                            it.copy(
                                appInitializedState = state,
                                isLoading = true
                            )
                        }

                        Idle -> updateState {
                            it.copy(
                                appInitializedState = state,
                                isLoading = false
                            )
                        }
                    }
                }
            }
            launch(dispatcherProvider.io) {
                activeRouteStore.routeIdFlow().collectLatest {
                    val route = repository.getRouteBy(it)
                    updateState { it.copy(selectedRoute = route) }
                }
            }
            launch(dispatcherProvider.io) {
                locationServiceStateEmitter.state.collectLatest { state ->
                    updateState { it.copy(locationServiceState = state) }
                }
            }
        }
    }

    override fun onAction(action: RouteTrackingAction) {
        when (action) {
            Logout -> logoutUseCase()
            Refresh -> appDataInitializer.initialize()
            SelectRoute -> navigator.navigate(RouteSelection)
            StartRoute -> viewModelScope.launch {
                _events.trySend(StartTracking)
            }

            StopRoute -> viewModelScope.launch {
                _events.trySend(StopTracking)
            }
        }
    }

    data class RouteTrackingUiState(
        val appInitializedState: InitializationSate = Loading,
        val locationServiceState: LocationServiceState = LocationServiceState.Stopped,
        val selectedRoute: Route? = null,
        val isLoading: Boolean = false,
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
        data object StartTracking : RouteTrackingEvent
        data object StopTracking : RouteTrackingEvent
    }
}