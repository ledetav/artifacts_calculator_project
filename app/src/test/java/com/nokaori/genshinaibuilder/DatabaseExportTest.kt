package com.nokaori.genshinaibuilder

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nokaori.genshinaibuilder.data.local.AppDatabase
import com.nokaori.genshinaibuilder.data.local.entity.*
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DatabaseExportTest {

    private lateinit var db: AppDatabase
    
    // Путь, куда сохраним базу. 
    // user.dir в Codespaces указывает на корень проекта.
    private val dbPath = System.getProperty("user.dir") + "/genshin_debug.db"

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // Удаляем старую базу перед тестом, чтобы создавать с нуля
        val file = File(dbPath)
        if (file.exists()) file.delete()

        // Создаем ФАЙЛОВУЮ базу данных
        db = Room.databaseBuilder(context, AppDatabase::class.java, dbPath)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
        println("💾 DATABASE EXPORTED TO: $dbPath")
    }

    @Test
    fun generateDatabaseFile() = runTest {
        // 1. Заполняем Энциклопедию Артефактов
        val set = ArtifactSetEntity(
            id = 1,
            name = "Crimson Witch of Flames",
            rarities = listOf(4, 5),
            bonus2pc = "Pyro DMG Bonus +15%",
            bonus4pc = "Increases Overloaded and Burning DMG by 40%...",
            iconUrl = "http://example.com/witch.png"
        )
        db.artifactDao().insertArtifactSets(listOf(set))

        val piece = ArtifactPieceEntity(
            id = 101,
            setId = 1,
            slot = ArtifactSlot.FLOWER_OF_LIFE,
            name = "Witch's Flower of Blaze",
            iconUrl = "http://example.com/flower.png"
        )
        db.artifactDao().insertArtifactPieces(listOf(piece))

        // 2. Заполняем Энциклопедию Оружия
        val weapon = WeaponEntity(
            id = 11509,
            name = "Mist Splitter Reforged",
            type = WeaponType.SWORD,
            rarity = 5,
            baseAtkLvl1 = 48f,
            subStatType = StatType.CRIT_DMG,
            subStatBaseValue = 9.6f,
            atkCurveId = "5_HIGH",
            subStatCurveId = "5_CRIT",
            iconUrl = "http://example.com/sword.png"
        )
        db.weaponDao().insertWeapons(listOf(weapon))

        // 3. Заполняем Энциклопедию Персонажей
        val character = CharacterEntity(
            id = 10000046,
            name = "Hu Tao",
            rarity = 5,
            element = Element.PYRO,
            weaponType = WeaponType.POLEARM,
            baseHpLvl1 = 1211f,
            baseAtkLvl1 = 8f,
            baseDefLvl1 = 68f,
            ascensionStatType = StatType.CRIT_DMG,
            curveId = "5_HP_LOW",
            iconUrl = "hutao_icon",
            splashUrl = "hutao_splash"
        )
        db.characterDao().insertCharacters(listOf(character))

        println("✅ Data inserted successfully!")
    }
}