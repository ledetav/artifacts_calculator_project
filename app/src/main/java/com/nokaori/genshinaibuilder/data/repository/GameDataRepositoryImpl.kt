package com.nokaori.genshinaibuilder.data.repository

import android.util.Log
import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.local.dao.StatCurveDao
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.remote.mapper.mapTalentsAndConstellations
import com.nokaori.genshinaibuilder.data.remote.mapper.toEntity
import com.nokaori.genshinaibuilder.data.remote.mapper.toEntities
import com.nokaori.genshinaibuilder.data.remote.mapper.updateWithDetails
import com.nokaori.genshinaibuilder.data.remote.mapper.mapPromotions
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import kotlinx.coroutines.delay

class GameDataRepositoryImpl(
    private val characterDao: CharacterDao,
    private val statCurveDao: StatCurveDao,
    private val api: YattaApi
) : GameDataRepository {

    override suspend fun updateCharacters(): Result<Unit> {
        return try {
            Log.d("GameDataRepo", "Fetching ALL stat curves...")
            
            val avatarCurves = api.getAvatarCurves().toEntities()
            statCurveDao.insertCurves(avatarCurves)
            
            val weaponCurves = api.getWeaponCurves().toEntities()
            statCurveDao.insertCurves(weaponCurves)
            
            val relicCurves = api.getRelicCurves().toEntities()
            statCurveDao.insertCurves(relicCurves)
            
            Log.d("GameDataRepo", "Saved total curves: ${avatarCurves.size + weaponCurves.size + relicCurves.size}")

            Log.d("GameDataRepo", "Fetching character list...")
            val listResponse = api.getAvatarList()
            val dtoList = listResponse.data.items.values
            
            val basicEntities = dtoList.map { it.toEntity() }
            
            val entityMap = dtoList.zip(basicEntities).associate { (dto, entity) -> dto.id to entity }

            characterDao.insertCharacters(basicEntities)
            Log.d("GameDataRepo", "Saved ${basicEntities.size} basic characters.")

            var processedCount = 0
            
            dtoList.forEach { dto ->
                val safeId = dto.id ?: return@forEach
                
                try {
                    val detailResponse = api.getAvatarDetail(safeId)
                    val detailDto = detailResponse.data
                    
                    val currentEntity = entityMap[safeId]
                    
                    if (currentEntity != null) {
                        val updatedEntity = currentEntity.updateWithDetails(detailDto)
                        characterDao.insertCharacters(listOf(updatedEntity))
                        
                        val charId = updatedEntity.id

                        val (talents, constellations) = mapTalentsAndConstellations(charId, detailDto)
                        characterDao.insertTalents(talents)
                        characterDao.insertConstellations(constellations)

                        val promotions = mapPromotions(charId, detailDto)
                        characterDao.insertPromotions(promotions)
                    }
                    
                    processedCount++
                    if (processedCount % 10 == 0) {
                        Log.d("GameDataRepo", "Updated details for $processedCount characters...")
                    }
                    
                    delay(50)

                } catch (e: Exception) {
                    Log.e("GameDataRepo", "Failed to update detail for ${dto.name} ($safeId)", e)
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