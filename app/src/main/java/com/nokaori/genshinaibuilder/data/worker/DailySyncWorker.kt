package com.nokaori.genshinaibuilder.data.worker

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val gameDataRepository: GameDataRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Запускаем как foreground-задачу чтобы Android не убил процесс на 12+
        setForeground(getForegroundInfo())
        return try {
            var isSuccess = false
            gameDataRepository.updateGameData().collect { status ->
                when (status) {
                    is SyncStatus.Success -> {
                        isSuccess = true
                        if (status.newChars + status.newWeapons + status.newArtifacts > 0) {
                            UpdateNotificationHelper.showUpdateNotification(
                                context = applicationContext,
                                newChars = status.newChars,
                                newWeapons = status.newWeapons,
                                newArtifacts = status.newArtifacts,
                                sampleCharNames = status.sampleCharNames,
                                sampleWeaponNames = status.sampleWeaponNames,
                                sampleArtifactNames = status.sampleArtifactNames
                            )
                        }
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

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            SYNC_NOTIFICATION_ID,
            buildSyncNotification()
        )
    }

    private fun buildSyncNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, UpdateNotificationHelper.SYNC_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.notif_sync_in_progress_title))
            .setContentText(applicationContext.getString(R.string.notif_sync_in_progress_body))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val SYNC_NOTIFICATION_ID = 1002
    }
}
