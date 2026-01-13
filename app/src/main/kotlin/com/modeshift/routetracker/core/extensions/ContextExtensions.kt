package com.modeshift.routetracker.core.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import timber.log.Timber

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

fun Context.isLocationEnabled(): Boolean {
    (getSystemService(Context.LOCATION_SERVICE) as LocationManager).let {
        return it.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}

fun Activity.enableLocation(
    onEnabled: () -> Unit,
    resolutionActivityResultLauncher: ActivityResultLauncher<IntentSenderRequest>,
) {
    val locationRequest = LocationRequest
        .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10)
        .setWaitForAccurateLocation(false)
        .build()
    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .setAlwaysShow(true)

    LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        .addOnSuccessListener {
            onEnabled()
        }
        .addOnFailureListener {
            (it as? ApiException)?.let {
                when (it.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        try {
                            val resolvable = it as ResolvableApiException
                            val intentSenderRequest =
                                IntentSenderRequest.Builder(resolvable.resolution).build()
                            resolutionActivityResultLauncher.launch(intentSenderRequest)
                        } catch (e: SendIntentException) {
                            Timber.e(e)
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }
}