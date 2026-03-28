package com.nokaori.genshinaibuilder.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object WorkerScheduler {

    private const val SYNC_WORK_NAME = "daily_game_data_sync"

    fun scheduleDailySync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val initialDelay = calculateInitialDelayTo12MSK()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailySyncWorker>(
            24, TimeUnit.HOURS,
            // flex window: задача может выполниться в последние 30 мин окна
            30, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        // UPDATE — пересчитывает initial delay каждый раз при старте приложения.
        // KEEP оставлял старый delay и задача никогда не попадала в 12:00 МСК.
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
    }

    private fun calculateInitialDelayTo12MSK(): Long {
        val mskTimeZone = TimeZone.getTimeZone("Europe/Moscow")
        val now = Calendar.getInstance(mskTimeZone)

        val target = Calendar.getInstance(mskTimeZone).apply {
            timeInMillis = now.timeInMillis
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Если сейчас уже позже 12:00 — назначаем на завтра
            if (now.timeInMillis >= timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        return target.timeInMillis - now.timeInMillis
    }
}
