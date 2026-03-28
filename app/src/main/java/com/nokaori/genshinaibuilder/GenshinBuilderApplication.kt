package com.nokaori.genshinaibuilder

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.nokaori.genshinaibuilder.data.worker.UpdateNotificationHelper
import com.nokaori.genshinaibuilder.data.worker.WorkerScheduler
import com.nokaori.genshinaibuilder.domain.usecase.OfflineDataUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okio.Path.Companion.toOkioPath
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GenshinBuilderApplication : Application(), SingletonImageLoader.Factory, Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var offlineDataUpdater: OfflineDataUpdater

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        UpdateNotificationHelper.createNotificationChannel(this)
        WorkerScheduler.scheduleDailySync(this)
        // OfflineDataUpdater проверяет prepackaged.db в assets и обновляет
        // игровые таблицы, если нужно. Room 2.8+ сам инициализирует
        // room_table_modification_log при первом DAO-вызове через свой API.
        applicationScope.launch(Dispatchers.IO) {
            offlineDataUpdater.runIfNeeded()
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache").toOkioPath())
                    .maxSizePercent(0.02)
                    .build()
            }
            .crossfade(true) 
            .logger(DebugLogger()) 
            .build()
    }
}