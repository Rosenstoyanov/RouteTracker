package com.modeshift.routetracker.utils

object Constants {
    const val DEFAULT_NOTIFICATION_CHANNEL_ID = "route_tracker_channel_default"
    const val STOPS_REACHED_NOTIFICATION_CHANNEL_ID = "route_tracker_stops_reached_channel"

    // TODO: Can be extracted in FirebaseRemoteConfig if needed
    const val DELTA_RADIUS_TO_STOP_DB_LOOK_UP_IN_METERS = 2000
    const val LOCATION_UPDATE_INTERVAL_IN_MILLISECONDS = 1
    const val LOCATION_UPDATE_BUFFER_CAPACITY = 100
    const val HAS_SIGNIFICANT_LOCATION_CHANGE_DELTA_IN_METERS = 30f


    const val STOP_EVENTS_UPLOAD_BATCH_SIZE = 100
    const val NETWORK_CALL_TIMEOUT_IN_SECONDS = 10
    const val NETWORK_CONNECT_TIMEOUT_IN_SECONDS = 3
}