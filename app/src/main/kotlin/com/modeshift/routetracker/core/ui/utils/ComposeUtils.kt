package com.modeshift.routetracker.core.ui.utils

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.modeshift.routetracker.core.ui.theme.RtTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

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
fun <T> ObserveAsEvents(flow: Flow<T>, onEvent: (T) -> Unit) {
    val livecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, livecycleOwner.lifecycle) {
        livecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}

@Composable
fun RtResumeDetector(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onResume: () -> Unit,
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    onResume()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
fun RtPreview(content: @Composable () -> Unit) {
    RtTheme {
        Surface {
            content()
        }
    }
}