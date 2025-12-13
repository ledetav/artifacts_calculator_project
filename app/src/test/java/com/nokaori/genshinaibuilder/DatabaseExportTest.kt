package com.nokaori.genshinaibuilder

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nokaori.genshinaibuilder.data.local.AppDatabase
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.repository.GameDataRepositoryImpl
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DatabaseExportTest {

    private lateinit var db: AppDatabase
    private lateinit var api: YattaApi
    private val dbName = "genshin_debug.db"

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        db = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .allowMainThreadQueries()
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        api = Retrofit.Builder()
            .baseUrl("https://gi.yatta.moe/api/v2/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YattaApi::class.java)
    }

    @After
    @Throws(IOException::class)
    fun closeAndExportDb() {
        db.close()
        
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbFile = context.getDatabasePath(dbName)
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
        val repository = GameDataRepositoryImpl(db.characterDao(), db.statCurveDao(), db.weaponDao(), api)
        val result = repository.updateCharacters()
        
        if (result.isSuccess) {
            println("✅ Characters populated from API")
        } else {
            println("❌ Failed: ${result.exceptionOrNull()?.message}")
        }
    }
}