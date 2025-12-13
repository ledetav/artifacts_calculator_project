package com.nokaori.genshinaibuilder.di

import android.content.Context
import androidx.room.Room
import com.nokaori.genshinaibuilder.data.local.AppDatabase
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.repository.ArtifactRepositoryImpl
import com.nokaori.genshinaibuilder.data.repository.CharacterRepositoryImpl
import com.nokaori.genshinaibuilder.data.repository.GameDataRepositoryImpl
import com.nokaori.genshinaibuilder.data.repository.ThemeRepositoryImpl
import com.nokaori.genshinaibuilder.data.repository.WeaponRepositoryImpl
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Интерфейс контейнера (полезно для подмены в тестах)
 */
interface AppContainer {
    val artifactRepository: ArtifactRepository
    val weaponRepository: WeaponRepository
    val characterRepository: CharacterRepository
    val themeRepository: ThemeRepository
    val gameDataRepository: GameDataRepository
}

/**
 * Реализация контейнера
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    // DATABASE (Singleton)
    // Создается один раз при первом обращении
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "genshin_optimizer.db")
            .fallbackToDestructiveMigration(true) // Полезно на этапе разработки
            .build()
    }

    // NETWORK API (Singleton)
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://gi.yatta.moe/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val yattaApi: YattaApi by lazy {
        retrofit.create(YattaApi::class.java)
    }

    // REPOSITORIES
    // Связываем DAO из базы и API из ретрофита с репозиториями
    override val artifactRepository: ArtifactRepository by lazy {
        ArtifactRepositoryImpl(database.artifactDao(), database.userDao())
    }

    override val weaponRepository: WeaponRepository by lazy {
        WeaponRepositoryImpl(database.weaponDao(), database.userDao())
    }

    override val characterRepository: CharacterRepository by lazy {
        CharacterRepositoryImpl(database.characterDao(), database.userDao())
    }

    override val themeRepository: ThemeRepository by lazy {
        ThemeRepositoryImpl(context)
    }

    override val gameDataRepository: GameDataRepository by lazy {
        // Передаем и DAO, и API
        GameDataRepositoryImpl(
            database.characterDao(), 
            database.statCurveDao(), 
            database.weaponDao(), 
            yattaApi
        ) 
    }
}