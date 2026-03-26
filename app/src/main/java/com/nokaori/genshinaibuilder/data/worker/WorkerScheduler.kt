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

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailySyncWorker>(
            24, TimeUnit.HOURS // Repeat every 24 hours
        )
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelayTo12MSK(), TimeUnit.MILLISECONDS)
            .build()

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
            // if we are already past 12:00 MSK today, schedule for tomorrow
            if (get(Calendar.HOUR_OF_DAY) >= 12) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val initialDelay = target.timeInMillis - now.timeInMillis
        return if (initialDelay < 0) 0 else initialDelay
    }
}
