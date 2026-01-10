package com.modeshift.routetracker.ui.route_selection

import com.modeshift.routetracker.core.BaseViewModel
import com.modeshift.routetracker.navigation.Navigator
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionAction
import com.modeshift.routetracker.ui.route_selection.RouteSelectionViewModel.RouteSelectionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteSelectionViewModel @Inject constructor(
    private val navigator: Navigator
) : BaseViewModel<RouteSelectionUiState, RouteSelectionAction>(RouteSelectionUiState()) {

    override fun onAction(action: RouteSelectionAction) {
        TODO("Not yet implemented")
    }

    data class RouteSelectionUiState(
        val error: String? = null
    )

    sealed interface RouteSelectionAction {

    }
}