package com.nokaori.genshinaibuilder.data.repository

import android.util.Log
import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.remote.mapper.mapTalentsAndConstellations
import com.nokaori.genshinaibuilder.data.remote.mapper.toEntity
import com.nokaori.genshinaibuilder.data.remote.mapper.updateWithDetails
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import kotlinx.coroutines.delay

class GameDataRepositoryImpl(
    private val characterDao: CharacterDao,
    private val api: YattaApi
) : GameDataRepository {

    override suspend fun updateCharacters(): Result<Unit> {
        return try {
            Log.d("GameDataRepo", "Fetching character list...")
            val listResponse = api.getAvatarList()
            val dtoList = listResponse.data.items.values
            
            // 1. Создаем Entity
            val basicEntities = dtoList.map { it.toEntity() }
            
            // 2. ВАЖНО: Создаем карту для быстрого и точного поиска
            // Ключ: ID из JSON (String, например "10000005-pyro")
            // Значение: Наша Entity (с ID 1000000500)
            val entityMap = dtoList.zip(basicEntities).associate { (dto, entity) -> dto.id to entity }

            // Сохраняем болванки
            characterDao.insertCharacters(basicEntities)
            
            var processedCount = 0
            
            dtoList.forEach { dto ->
                val safeId = dto.id ?: return@forEach
                
                try {
                    // ... запрос деталей ...
                    val detailResponse = api.getAvatarDetail(safeId)
                    val detailDto = detailResponse.data
                    
                    // 3. ИСПРАВЛЕНИЕ: Ищем не по имени, а берем из карты
                    // Это гарантирует, что детали Пиро ГГ попадут именно в Пиро ГГ
                    val currentEntity = entityMap[safeId] // safeId == dto.id
                    
                    if (currentEntity != null) {
                        val updatedEntity = currentEntity.updateWithDetails(detailDto)
                        
                        characterDao.insertCharacters(listOf(updatedEntity))
                        
                        val charId = updatedEntity.id
                        val (talents, constellations) = mapTalentsAndConstellations(charId, detailDto)
                        
                        characterDao.insertTalents(talents)
                        characterDao.insertConstellations(constellations)
                    }
                    
                    processedCount++
                    if (processedCount % 10 == 0) Log.d("GameDataRepo", "Updated $processedCount...")
                    
                    delay(50) 

                } catch (e: Exception) {
                    Log.e("GameDataRepo", "Failed to update detail for ${dto.name}", e)
                }
            }

            Log.d("GameDataRepo", "Update Complete!")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e("GameDataRepo", "Global Update Error", e)
            Result.failure(e)
        }
    }
}