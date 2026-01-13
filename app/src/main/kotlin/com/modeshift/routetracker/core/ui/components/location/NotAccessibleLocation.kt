package com.modeshift.routetracker.core.ui.components.location

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.modeshift.routetracker.R
import com.modeshift.routetracker.core.ui.utils.RtPreview
import com.modeshift.routetracker.core.ui.utils.debounceClick

@Composable
fun NotAccessibleLocation(
    modifier: Modifier = Modifier,
    allowLocation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.4f))

        Icon(
            modifier = Modifier.size(128.dp),
            painter = painterResource(id = R.drawable.outline_location_on_24),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Enable Location Access")

        Spacer(modifier = Modifier.weight(0.3f))

        Button(debounceClick { allowLocation() }) {
            Text("Allow Location")
        }

        Spacer(modifier = Modifier.weight(0.3f))
    }
}

@Preview
@Composable
private fun NotAccessibleLocationPreview() {
    RtPreview {
        NotAccessibleLocation {}
    }
}