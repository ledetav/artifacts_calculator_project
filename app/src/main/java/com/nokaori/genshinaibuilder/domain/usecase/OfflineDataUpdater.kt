package com.nokaori.genshinaibuilder.domain.usecase

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.nokaori.genshinaibuilder.data.local.AppDatabase
import com.nokaori.genshinaibuilder.data.repository.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OfflineDataUpdater
 *
 * При каждом запуске приложения проверяет версию встроенных игровых данных.
 * Если в коде задана более новая версия — обновляет ТОЛЬКО игровые таблицы
 * из prepackaged.db (assets), не затрагивая пользовательские данные.
 *
 * Как обновить данные в следующем патче:
 * 1. Запустить GenerateOfflineDbTest → появится новый prepackaged.db в assets.
 * 2. Увеличить CURRENT_OFFLINE_DB_VERSION на 1.
 * 3. Собрать и выпустить APK.
 * При следующем запуске INSERT OR REPLACE накатит новые данные.
 */
@Singleton
class OfflineDataUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase
) {
    companion object {
        /**
         * Текущая версия встроенных игровых данных.
         * Увеличьте на 1 при каждом обновлении prepackaged.db.
         */
        const val CURRENT_OFFLINE_DB_VERSION = 2

        private const val TAG = "OfflineDataUpdater"
        private val KEY_OFFLINE_DB_VERSION = intPreferencesKey("offline_db_version")

        /** Игровые таблицы, которые обновляются из prepackaged.db */
        private val GAME_TABLES = listOf(
            "stat_curves",
            "characters_data",
            "character_promotions",
            "character_constellations",
            "character_talents",
            "weapons_data",
            "weapon_promotions",
            "weapon_refinements",
            "artifact_sets_data",
            "artifact_pieces_data",
            "sync_metadata"
        )
    }

    /**
     * Вызывать при старте приложения (в coroutine scope).
     * Если prepackaged.db отсутствует или версия не изменилась — ничего не делает.
     */
    suspend fun runIfNeeded() = withContext(Dispatchers.IO) {
        // 1. Проверяем наличие prepackaged.db в assets
        val hasAsset = try {
            context.assets.open("prepackaged.db").close()
            true
        } catch (_: Exception) {
            false
        }
        if (!hasAsset) {
            Log.d(TAG, "prepackaged.db not found in assets, skipping update.")
            return@withContext
        }

        // 2. Сравниваем версии
        val installedVersion = context.dataStore.data.first()[KEY_OFFLINE_DB_VERSION] ?: 0
        if (installedVersion >= CURRENT_OFFLINE_DB_VERSION) {
            Log.d(TAG, "Offline DB is up to date (v$installedVersion). Skipping.")
            return@withContext
        }

        Log.i(TAG, "Offline DB update: v$installedVersion → v$CURRENT_OFFLINE_DB_VERSION. Starting...")

        try {
            // 3. Копируем asset во временный файл (ATTACH DATABASE требует реальный путь)
            val tmpFile = File(context.cacheDir, "prepackaged_tmp.db")
            context.assets.open("prepackaged.db").use { input ->
                tmpFile.outputStream().use { output -> input.copyTo(output) }
            }

            // 4. Открываем Room's WritableDatabase и подключаем временную БД
            val sqLiteDb = db.openHelper.writableDatabase
            sqLiteDb.execSQL("ATTACH DATABASE '${tmpFile.absolutePath}' AS asset_db")

            try {
                sqLiteDb.beginTransaction()
                // 5. INSERT OR REPLACE по каждой игровой таблице
                for (table in GAME_TABLES) {
                    sqLiteDb.execSQL(
                        "INSERT OR REPLACE INTO main.$table SELECT * FROM asset_db.$table"
                    )
                    Log.d(TAG, "Updated table: $table")
                }
                sqLiteDb.setTransactionSuccessful()
                Log.i(TAG, "All game tables updated successfully.")
            } finally {
                sqLiteDb.endTransaction()
                sqLiteDb.execSQL("DETACH DATABASE asset_db")
            }

            // 6. Удаляем временный файл
            tmpFile.delete()

            // 7. Сохраняем новую версию и время синхронизации в DataStore
            val dbTimestamp = db.syncMetadataDao().getValue("last_updated_at") ?: System.currentTimeMillis()
            
            context.dataStore.edit { prefs ->
                prefs[KEY_OFFLINE_DB_VERSION] = CURRENT_OFFLINE_DB_VERSION
                // Используем longPreferencesKey("last_sync_time") напрямую или через репозиторий
                // Но лучше просто добавить ключ здесь для инкапсуляции обновления
                prefs[longPreferencesKey("last_sync_time")] = dbTimestamp
            }
            Log.i(TAG, "Offline DB version saved: v$CURRENT_OFFLINE_DB_VERSION, timestamp: $dbTimestamp")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to update offline game data", e)
        }
    }
}
