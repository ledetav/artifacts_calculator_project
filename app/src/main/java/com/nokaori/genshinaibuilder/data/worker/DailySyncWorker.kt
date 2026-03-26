package com.nokaori.genshinaibuilder.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class DailySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val gameDataRepository: GameDataRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Collecting until the sync finishes
            var isSuccess = false
            gameDataRepository.updateGameData().collect { status ->
                when (status) {
                    is SyncStatus.Success -> {
                        isSuccess = true
                    }
                    is SyncStatus.Error -> {
                        isSuccess = false
                    }
                    else -> {} // Still in progress
                }
            }
            if (isSuccess) Result.success() else Result.retry()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
