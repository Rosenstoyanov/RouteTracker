package com.modeshift.routetracker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.modeshift.routetracker.R
import com.modeshift.routetracker.core.ui.utils.RtPreview
import com.modeshift.routetracker.core.ui.utils.debounceClick
import com.modeshift.routetracker.domain.models.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RtRoadSelectionBar(
    routes: List<Route>,
    onRouteSelected: (Route) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf<String?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(start = 12.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_directions_bus_24),
                contentDescription = null,
                tint = Color.Gray
            )

            Text(
                text = selectedText ?: stringResource(R.string.select_route),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (selectedText == null) Color.Gray else Color.Black
            )

            IconButton(debounceClick { onLogout() }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_logout_24),
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LazyColumn {
                items(
                    items = routes,
                    key = { it.id }
                ) { route ->
                    DropdownMenuItem(
                        text = { Text(text = route.toLabel()) },
                        onClick = {
                            selectedText = route.toLabel()
                            expanded = false
                            onRouteSelected(route)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RtRoadSelectionBarPreview() {
    RtPreview {
        RtRoadSelectionBar(
            routes = listOf(
                Route(id = 0, name = "94", description = "Student City - Sofia University"),
                Route(id = 1, name = "110", description = "Student City - NDK"),
            ),
            onRouteSelected = {},
            onLogout = {}
        )
    }
}