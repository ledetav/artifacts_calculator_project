package com.nokaori.genshinaibuilder.data.repository

import android.util.Log
import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
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
            
            val basicEntities = dtoList.map { it.toEntity() }
            
            characterDao.insertCharacters(basicEntities)
            Log.d("GameDataRepo", "Saved ${basicEntities.size} basic characters.")

            var processedCount = 0
            
            dtoList.forEach { dto ->
                val safeId = dto.id ?: return@forEach
                
                try {
                    val detailResponse = api.getAvatarDetail(safeId)
                    val detailDto = detailResponse.data
                    
                    val currentEntity = basicEntities.find { it.name == dto.name }
                    
                    if (currentEntity != null) {
                        val updatedEntity = currentEntity.updateWithDetails(detailDto)
                        
                        characterDao.insertCharacters(listOf(updatedEntity))
                    }
                    
                    processedCount++
                    if (processedCount % 10 == 0) {
                        Log.d("GameDataRepo", "Updated details for $processedCount characters...")
                    }
                    
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