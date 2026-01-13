package com.modeshift.routetracker.core.ui.components.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.modeshift.routetracker.core.extensions.isLocationEnabled
import timber.log.Timber

@Composable
fun LocationAvailabilityDetector(
    onLocationStatusChanged: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    var isReceiverRegistered by remember { mutableStateOf(false) }
    val locationSwitchStateReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {
                    onLocationStatusChanged(context.isLocationEnabled())
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                try {
                    val intentFilter = IntentFilter().apply {
                        addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.registerReceiver(
                            locationSwitchStateReceiver,
                            intentFilter,
                            Context.RECEIVER_EXPORTED
                        )
                    } else {
                        context.registerReceiver(locationSwitchStateReceiver, intentFilter)
                    }
                    isReceiverRegistered = true
                } catch (e: Exception) {
                    Timber.e(e)
                }

            } else if (event == Lifecycle.Event.ON_STOP) {
                try {
                    context.unregisterReceiver(locationSwitchStateReceiver)
                    isReceiverRegistered = false
                } catch (e: IllegalArgumentException) {
                    Timber.e(e)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (isReceiverRegistered) {
                try {
                    context.unregisterReceiver(locationSwitchStateReceiver)
                    isReceiverRegistered = false
                } catch (e: IllegalArgumentException) {
                    Timber.e(e)
                }
            }
        }
    }
}