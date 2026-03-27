package com.nokaori.genshinaibuilder.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nokaori.genshinaibuilder.R

/**
 * Helper for creating the encyclopedia-update notification channel
 * and posting update notifications when new game data is synced.
 */
object UpdateNotificationHelper {

    private const val CHANNEL_ID = "encyclopedia_updates"
    private const val NOTIFICATION_ID = 1001

    /** Call once from Application.onCreate() to register the channel (safe to call multiple times). */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notif_update_channel_name)
            val description = context.getString(R.string.notif_update_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Shows a notification summarising newly added game data.
     * Does nothing if all counts are zero.
     */
    fun showUpdateNotification(
        context: Context,
        newChars: Int,
        newWeapons: Int,
        newArtifacts: Int,
        sampleCharNames: List<String> = emptyList(),
        sampleWeaponNames: List<String> = emptyList(),
        sampleArtifactNames: List<String> = emptyList()
    ) {
        if (newChars + newWeapons + newArtifacts == 0) return

        val parts = mutableListOf<String>()

        if (newChars > 0) {
            val quantityStr = context.resources.getQuantityString(
                R.plurals.notif_update_chars, newChars, newChars
            )
            parts += buildPart(context, quantityStr, sampleCharNames)
        }
        if (newWeapons > 0) {
            val quantityStr = context.resources.getQuantityString(
                R.plurals.notif_update_weapons, newWeapons, newWeapons
            )
            parts += buildPart(context, quantityStr, sampleWeaponNames)
        }
        if (newArtifacts > 0) {
            val quantityStr = context.resources.getQuantityString(
                R.plurals.notif_update_artifacts, newArtifacts, newArtifacts
            )
            parts += buildPart(context, quantityStr, sampleArtifactNames)
        }

        val prefix = context.getString(R.string.notif_update_body_prefix)
        val body = "$prefix ${parts.joinToString(", ")}."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.notif_update_title))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS not granted — silently skip (expected on Android 13+ if user denied)
        }
    }

    /** Appends sample names to a quantity string, e.g. "2 новых персонажа (Нахида и другие)". */
    private fun buildPart(
        context: Context,
        quantityStr: String,
        samples: List<String>
    ): String {
        if (samples.isEmpty()) return quantityStr
        val more = context.getString(R.string.notif_update_sample_more)
        val sampleText = if (samples.size == 1) {
            samples.first()
        } else {
            "${samples.first()} $more"
        }
        return "$quantityStr ($sampleText)"
    }
}
