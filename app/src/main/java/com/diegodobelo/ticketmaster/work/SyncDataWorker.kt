package com.diegodobelo.ticketmaster.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.diegodobelo.ticketmaster.usecases.SyncDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val syncDataUseCase: SyncDataUseCase
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val result = syncDataUseCase("")
        if (result.isFailure) {
            return Result.retry()
        }
        return Result.success()
    }
}