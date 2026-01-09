package com.modeshift.routetracker.core.ui.utils

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.modeshift.routetracker.core.ui.theme.RouteTrackerTheme

private const val DEFAULT_THROTTLE_TIME = 750L

@Composable
fun debounceClick(
    throttleTime: Long = DEFAULT_THROTTLE_TIME,
    onClick: () -> Unit,
): () -> Unit {
    var lastClickTime = remember { 0L }

    return {
        if (SystemClock.elapsedRealtime() - lastClickTime > throttleTime) {
            onClick()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    }
}

fun Modifier.debounceClickable(
    throttleTime: Long = DEFAULT_THROTTLE_TIME,
    enabled: Boolean = true,
    onClick: () -> Unit,
) = composed {
    var lastClickTime: Long = remember { 0 }
    Modifier.clickable(
        enabled = enabled,
        onClick = {
            if (SystemClock.elapsedRealtime() - lastClickTime > throttleTime) {
                onClick()
                lastClickTime = SystemClock.elapsedRealtime()
            }
        },
    )
}

@Composable
fun TgPreview(content: @Composable () -> Unit) {
    RouteTrackerTheme {
        Surface {
            content()
        }
    }
}