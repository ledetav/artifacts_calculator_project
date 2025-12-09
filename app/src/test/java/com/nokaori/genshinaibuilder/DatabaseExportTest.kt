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

// Используем Robolectric (JUnit 4)
@RunWith(RobolectricTestRunner::class)
// Убираем генерацию манифеста, если она мешает, но обычно для Room нужен Context
@Config(manifest = Config.NONE) // Можно явно указать SDK
class DatabaseExportTest {

    private lateinit var db: AppDatabase
    private val dbName = "genshin_debug.db"

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // allowMainThreadQueries нужен, так как Robolectric иногда блокирует потоки иначе
        db = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .allowMainThreadQueries()
            .build()
    }

    @After
    @Throws(IOException::class)
    fun closeAndExportDb() {
        db.close()
        
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbFile = context.getDatabasePath(dbName)
        
        // Сохраняем прямо в корень проекта, чтобы легко найти
        val projectDir = System.getProperty("user.dir")
        val destFile = File(projectDir, dbName)

        if (dbFile.exists()) {
            dbFile.copyTo(destFile, overwrite = true)
            println("\n==================================================")
            println("DATABASE EXPORTED SUCCESSFULLY!")
            println("Location: ${destFile.absolutePath}")
            println("==================================================\n")
        } else {
            println("\n❌ Error: Database file was not created at ${dbFile.absolutePath}\n")
        }
    }

    @Test
    fun populateAndExport() = runTest {
        val dao = db.artifactDao()

        val set = ArtifactSetEntity(
            id = 15001,
            name = "Gladiator's Finale",
            rarities = listOf(4, 5),
            bonus2pc = "+18% ATK",
            bonus4pc = "Normal Attack DMG +35%",
            iconUrl = "url_flower"
        )
        
        dao.insertArtifactSets(listOf(set))

        println("Data inserted into DB.")
    }
}