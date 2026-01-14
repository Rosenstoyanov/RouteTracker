package com.modeshift.routetracker.event_sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopEventsSyncScheduler @Inject constructor(
    @ApplicationContext
    private val appContext: Context
) {
    private val workManager by lazy { WorkManager.getInstance(appContext) }

    fun scheduleOneTimeRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadEventsRequest = OneTimeWorkRequestBuilder<UploadStopEventsWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .addTag(EVENT_UPLOAD_JOB_TAG)
            .build()

        workManager.enqueueUniqueWork(
            EVENT_UPLOAD_UNIQUE_WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            uploadEventsRequest
        )
    }

    fun scheduleDailyStopEventsUpload() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val uploadEventsRequest = PeriodicWorkRequestBuilder<UploadStopEventsWorker>(
            24, TimeUnit.HOURS,
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(EVENT_DAILY_UPLOAD_JOB_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            EVENT_DAILY_UPLOAD_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            uploadEventsRequest
        )
    }

    companion object Companion {
        private const val EVENT_UPLOAD_JOB_TAG = "upload_events"
        private const val EVENT_UPLOAD_UNIQUE_WORK_NAME = "sync_stop_events_job"
        private const val EVENT_DAILY_UPLOAD_JOB_TAG = "daily_upload_events"
        private const val EVENT_DAILY_UPLOAD_WORK_NAME = "daily_sync_stop_events_job"

    }
}