package com.modeshift.routetracker.core.ui.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.modeshift.routetracker.domain.models.Stop


data class StopPin(
    val pinPosition: LatLng,
    val pinTitle: String,
    val stop: Stop,
) : ClusterItem {
    override fun getPosition(): LatLng = pinPosition

    override fun getTitle(): String = pinTitle

    override fun getSnippet(): String? = null

    override fun getZIndex(): Float? = null
}