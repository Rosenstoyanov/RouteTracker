package com.modeshift.routetracker.event_sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class UploadStopEventsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val stopEventsSyncExecutor: StopEventsSyncExecutor
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            stopEventsSyncExecutor.executeDirectly()
            Result.success()
        } catch (e: Exception) {
            if (isStopped || e is CancellationException) {
                return Result.retry()
            }

            return if (runAttemptCount > 5) {
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }
}