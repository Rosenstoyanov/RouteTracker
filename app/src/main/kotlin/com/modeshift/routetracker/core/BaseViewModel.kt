package com.modeshift.routetracker.core

import androidx.lifecycle.ViewModel
import com.modeshift.routetracker.core.extensions.updateAndLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<UiState, Action>(
    initialState: UiState
) : ViewModel() {
    private var _uiState: MutableStateFlow<UiState> = MutableStateFlow(initialState)
    val uiState: StateFlow<UiState> = _uiState

    protected fun updateState(stateUpdate: (UiState) -> UiState) {
        _uiState.updateAndLog { stateUpdate(it) }
    }

    open fun onAction(action: Action) {}
}