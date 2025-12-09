package com.nokaori.genshinaibuilder

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nokaori.genshinaibuilder.data.local.AppDatabase
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.IOException
import kotlin.jvm.Throws

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DatabaseExportTest {

    private lateinit var db: AppDatabase
    private val dbName = "genshin_debug.db" // Имя файла

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // ВАЖНО: Используем databaseBuilder вместо inMemoryDatabaseBuilder
        // Это заставит Room создать реальный файл
        db = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .allowMainThreadQueries()
            .build()
    }

    @After
    @Throws(IOException::class)
    fun closeAndExportDb() {
        db.close()
        
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // 1. Находим, куда Robolectric спрятал файл базы
        val dbFile = context.getDatabasePath(dbName)
        
        // 2. Определяем путь в корне проекта
        // System.getProperty(\"user.dir\") в Gradle тестах указывает на папку проекта
        val destFile = File(System.getProperty("user.dir"), dbName)

        // 3. Копируем файл
        if (dbFile.exists()) {
            dbFile.copyTo(destFile, overwrite = true)
            println("--------------------------------------------------")
            println("🎉 DATABASE EXPORTED SUCCESSFULY!")
            println("📂 Location: ${destFile.absolutePath}")
            println("⬇️  Now you can download '$dbName' from your file explorer.")
            println("--------------------------------------------------")
        } else {
            println("❌ Error: Database file was not created.")
        }
    }

    @Test
    fun populateAndExport() = runTest {
        // Чтобы файл создался, нужно обязательно что-то записать!
        val dao = db.artifactDao()

        // Добавляем тестовые данные, чтобы таблица не была пустой
        val set = ArtifactSetEntity(
            id = 15001,
            name = "Gladiator's Finale",
            rarities = listOf(4, 5),
            bonus2pc = "+18% ATK",
            bonus4pc = "Normal Attack DMG +35%",
            iconUrl = "url_flower"
        )
        dao.insertArtifactSets(listOf(set))
        
        println("✅ Data inserted.")
    }
}
