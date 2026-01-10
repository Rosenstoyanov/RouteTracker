package com.modeshift.routetracker.ui.route_tracking

import com.modeshift.routetracker.core.BaseViewModel
import com.modeshift.routetracker.navigation.Navigator
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingAction
import com.modeshift.routetracker.ui.route_tracking.RouteTrackingViewModel.RouteTrackingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteTrackingViewModel @Inject constructor(
    private val navigator: Navigator
) : BaseViewModel<RouteTrackingUiState, RouteTrackingAction>(RouteTrackingUiState()) {

    override fun onAction(action: RouteTrackingAction) {
        TODO("Not yet implemented")
    }

    data class RouteTrackingUiState(
        val error: String? = null
    )

    sealed interface RouteTrackingAction {

    }
}