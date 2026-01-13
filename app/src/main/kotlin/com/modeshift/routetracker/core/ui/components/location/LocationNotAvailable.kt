package com.modeshift.routetracker.core.ui.components.location

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.modeshift.routetracker.core.extensions.enableLocation
import com.modeshift.routetracker.core.extensions.hasLocationPermission

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationNotAvailable(
    modifier: Modifier = Modifier,
    showLocationPermissionNotGranted: Boolean = false,
    showLocationNotEnabled: Boolean = false,
    onLocationEnabled: () -> Unit = {},
) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        onPermissionResult = { isGranted ->
            if (isGranted) onLocationEnabled()
        }
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) onLocationEnabled()
        }
    )
    val appSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _: ActivityResult ->
        if (context.hasLocationPermission()) {
            onLocationEnabled()
        }
    }

    NotAccessibleLocation(modifier) {
        if (showLocationNotEnabled) {
            (context as Activity).enableLocation(
                onEnabled = onLocationEnabled,
                resolutionActivityResultLauncher = launcher
            )
        }
        if (showLocationPermissionNotGranted) {
            if (!locationPermissionState.status.shouldShowRationale) {
                locationPermissionState.launchPermissionRequest()
            } else {
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                appSettingsLauncher.launch(intent)
            }
        }
    }
}