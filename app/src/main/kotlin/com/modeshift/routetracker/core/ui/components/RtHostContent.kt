package com.modeshift.routetracker.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun RtHostContent(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    showLoading: Boolean = false,
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.imePadding(),
                hostState = snackBarHostState
            )
        },
        content = { paddingValues ->
            Box {
                content(paddingValues)
                if (showLoading) {
                    RtDimmedLoader(modifier = Modifier.matchParentSize())
                }
            }
        }
    )
}