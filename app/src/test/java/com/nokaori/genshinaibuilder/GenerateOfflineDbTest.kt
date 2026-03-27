package com.nokaori.genshinaibuilder

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nokaori.genshinaibuilder.data.local.AppDatabase
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.repository.GameDataRepositoryImpl
import com.nokaori.genshinaibuilder.domain.model.SupportedLanguages
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * GenerateOfflineDbTest
 *
 * Robolectric-тест, который делает реальные запросы к Yatta API,
 * заполняет Room-базу данными для EN и RU,
 * а затем копирует её в app/src/main/assets/prepackaged.db.
 *
 * ВАЖНО: этот тест делает реальные сетевые запросы — запускать только
 * через workflow generate-db.yml или вручную командой:
 *   ./gradlew :app:test --tests "*.GenerateOfflineDbTest"
 */
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [33]
)
class GenerateOfflineDbTest {

    private lateinit var db: AppDatabase
    private lateinit var api: YattaApi

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()

        // Файловая БД (не in-memory!) — чтобы потом скопировать файл
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "genshin_optimizer.db"
        )
            .allowMainThreadQueries()
            .build()

        // Настоящий Retrofit с увеличенным таймаутом
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        api = Retrofit.Builder()
            .baseUrl("https://gi.yatta.moe/api/v2/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YattaApi::class.java)
    }

    @After
    fun tearDown() {
        if (db.isOpen) db.close()
    }

    @Test
    fun generateAndSavePrepackagedDb() = runBlocking {
        // Синхронизируем EN
        println("=== [GenerateOfflineDbTest] Синхронизация (EN)...")
        val repoEn = buildRepository(SupportedLanguages.EN)
        val statusEn = repoEn.updateGameData().first { it.isTerminal() }
        assertTrue("Синхронизация EN завершилась ошибкой: $statusEn", statusEn is SyncStatus.Success)
        println("=== [GenerateOfflineDbTest] EN готов ✓")

        // Синхронизируем RU (та же БД, добавляются строки с language = 'ru')
        println("=== [GenerateOfflineDbTest] Синхронизация (RU)...")
        val repoRu = buildRepository(SupportedLanguages.RU)
        val statusRu = repoRu.updateGameData().first { it.isTerminal() }
        assertTrue("Синхронизация RU завершилась ошибкой: $statusRu", statusRu is SyncStatus.Success)
        println("=== [GenerateOfflineDbTest] RU готов ✓")

        // Закрываем БД — обязательно перед копированием файла
        db.close()

        // Путь к сгенерированному файлу в Robolectric-окружении
        val context = ApplicationProvider.getApplicationContext<Application>()
        val dbFile = context.getDatabasePath("genshin_optimizer.db")

        assertTrue("Файл БД не найден: ${dbFile.absolutePath}", dbFile.exists())
        println("=== [GenerateOfflineDbTest] БД: ${dbFile.absolutePath} (${dbFile.length()} байт)")

        // Целевой путь в assets
        val assetsDir = resolveAssetsDir()
        assetsDir.mkdirs()
        val target = File(assetsDir, "prepackaged.db")

        dbFile.copyTo(target, overwrite = true)
        println("=== [GenerateOfflineDbTest] Скопирована → ${target.absolutePath} (${target.length()} байт)")

        assertTrue("Файл prepackaged.db не создан", target.exists())
        assertTrue("Файл prepackaged.db пустой", target.length() > 0)
        println("=== [GenerateOfflineDbTest] ГОТОВО ✅")
    }

    // ----------------------------------------------------------------
    // Вспомогательные функции
    // ----------------------------------------------------------------

    /**
     * Создаёт репозиторий с фиксированным языком через stub SettingsRepository.
     * Не меняет production-код — язык задаётся только для этого теста.
     */
    private fun buildRepository(language: String): GameDataRepositoryImpl {
        val stub = object : SettingsRepository {
            override val appLanguage: Flow<String> = flowOf(language)
            override val lastSyncTime: Flow<Long> = flowOf(0L)
            override suspend fun setAppLanguage(lang: String) {}
            override suspend fun setLastSyncTime(time: Long) {}
        }
        return GameDataRepositoryImpl(
            characterDao = db.characterDao(),
            statCurveDao = db.statCurveDao(),
            weaponDao = db.weaponDao(),
            artifactDao = db.artifactDao(),
            api = api,
            themeRepository = stub
        )
    }

    /**
     * Определяет путь к app/src/main/assets/ — работает и локально, и в GitHub Actions.
     */
    private fun resolveAssetsDir(): File {
        // В GitHub Actions: GITHUB_WORKSPACE задан автоматически
        val githubWorkspace = System.getenv("GITHUB_WORKSPACE")
        if (githubWorkspace != null) {
            return File("$githubWorkspace/app/src/main/assets")
        }
        // Локально: Gradle запускается из корня проекта
        val workDir = File(System.getProperty("user.dir") ?: ".")
        val fromRoot = File(workDir, "app/src/main/assets")
        val fromApp  = File(workDir, "src/main/assets")
        return if (fromRoot.parentFile?.exists() == true) fromRoot else fromApp
    }

    /** Терминальный статус Flow — Success или Error */
    private fun SyncStatus.isTerminal() = this is SyncStatus.Success || this is SyncStatus.Error
}
