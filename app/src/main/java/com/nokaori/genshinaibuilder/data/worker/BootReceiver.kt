package com.nokaori.genshinaibuilder.data.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Перерегистрирует ежедневную задачу синхронизации после перезагрузки устройства.
 *
 * WorkManager сам по себе пытается восстановить задачи, но на многих OEM-устройствах
 * (Xiaomi, Huawei, Samsung с агрессивными настройками батареи) это не срабатывает.
 * BroadcastReceiver с BOOT_COMPLETED гарантирует, что задача будет переподписана.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON" // HTC/некоторые Huawei
        ) {
            WorkerScheduler.scheduleDailySync(context)
        }
    }
}
