package com.modeshift.routetracker.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<UiState, Action>(
    initialState: UiState
) : ViewModel() {
    private var _uiState: MutableStateFlow<UiState> = MutableStateFlow(initialState)
    val uiState: StateFlow<UiState> = _uiState

    protected fun updateState(stateUpdate: (UiState) -> UiState) {
        _uiState.update { stateUpdate(it) }
    }

    open fun onAction(action: Action) {}
}