package com.nokaori.genshinaibuilder.data.worker

import android.content.Context
import android.util.Log
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
    private const val TAG = "WorkerScheduler"

    fun scheduleDailySync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 1. Сначала планируем OneTimeWorkRequest для "первого" запуска точно в 15:45 (ТЕСТ).
        // Это обходит баг WorkManager, когда initialDelay в PeriodicWorkRequest 
        // добавляется ПОВЕРХ первого периода (в итоге запуск был через 48ч вместо 24ч).
        val initialDelay = calculateInitialDelayTo1545MSK()
        val hours = initialDelay / 3_600_000
        val minutes = (initialDelay % 3_600_000) / 60_000
        Log.i(TAG, "Scheduling one-time sync for first run. Next run in ${hours}h ${minutes}m (delay=${initialDelay}ms)")

        val firstRunRequest = androidx.work.OneTimeWorkRequestBuilder<DailySyncWorker>()
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("first_sync_run")
            .build()

        // Используем REPLACE для теста, чтобы сразу увидеть изменения
        WorkManager.getInstance(context).enqueueUniqueWork(
            "first_daily_sync",
            androidx.work.ExistingWorkPolicy.REPLACE,
            firstRunRequest
        )

        // 2. Также планируем периодическую задачу на будущее. 
        // Она начнет свои циклы. Даже если она запустится чуть позже первого 
        //OneTime-воркера — не страшно, они используют одинаковый уникальный нейм внутри (если бы были одинаковы).
        // Но лучше: DailySyncWorker сам может перепланировать периодику, 
        // либо мы просто ставим Periodic с KEEP, чтобы он подхватил через 24ч.
        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailySyncWorker>(
            24, TimeUnit.HOURS,
            30, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        // REPLACE для теста
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyWorkRequest
        )
    }

    private fun calculateInitialDelayTo1545MSK(): Long {
        val mskTimeZone = TimeZone.getTimeZone("Europe/Moscow")
        val now = Calendar.getInstance(mskTimeZone)

        val target = Calendar.getInstance(mskTimeZone).apply {
            timeInMillis = now.timeInMillis
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 45)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Если сейчас уже позже 15:45 — планируем на завтра.
            if (now.timeInMillis >= timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        return target.timeInMillis - now.timeInMillis
    }
}

