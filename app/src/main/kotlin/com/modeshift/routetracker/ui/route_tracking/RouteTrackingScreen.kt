package com.modeshift.routetracker.ui.route_tracking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun RouteTrackingScreen(
    viewModel: RouteTrackingViewModel = hiltViewModel()
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Text(text = "Route Tracking Screen")
        }
    }
}

@Composable
private fun RouteTrackingContent() {

}

@Preview
@Composable
private fun RouteTrackingScreenPreview() {
    RouteTrackingScreen()
}
