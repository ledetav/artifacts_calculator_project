package com.nokaori.genshinaibuilder.data.repository

import android.util.Log
import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.remote.mapper.toEntity
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameDataRepositoryImpl(
    private val characterDao: CharacterDao
) : GameDataRepository {

    // Для ручного DI создаем здесь lazy.
    private val api: YattaApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://gi.yatta.moe/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YattaApi::class.java)
    }

    override suspend fun updateCharacters(): Result<Unit> {
        return try {
            // Сетевой запрос
            val response = api.getAvatarList()

            // Маппинг (DTO -> Entity)
            // Берем values из Map и превращаем в список Entity
            val entities = response.data.items.values.map { it.toEntity() }

            // Сохранение в БД
            if (entities.isNotEmpty()) {
                characterDao.insertCharacters(entities)
                Log.d("GameDataRepo", "Successfully updated ${entities.size} characters")
                Result.success(Unit)
            } else {
                Result.failure(Exception("API returned empty list"))
            }
        } catch (e: Exception) {
            Log.e("GameDataRepo", "Error updating characters", e)
            Result.failure(e)
        }
    }
}