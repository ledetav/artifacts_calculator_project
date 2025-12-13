package com.nokaori.genshinaibuilder.data.repository

import android.util.Log
import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.local.dao.StatCurveDao
import com.nokaori.genshinaibuilder.data.local.dao.WeaponDao
import com.nokaori.genshinaibuilder.data.local.dao.ArtifactDao
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.remote.mapper.mapTalentsAndConstellations
import com.nokaori.genshinaibuilder.data.remote.mapper.toEntity
import com.nokaori.genshinaibuilder.data.remote.mapper.toEntities
import com.nokaori.genshinaibuilder.data.remote.mapper.updateWithDetails
import com.nokaori.genshinaibuilder.data.remote.mapper.mapPromotions
import com.nokaori.genshinaibuilder.data.remote.mapper.mapWeaponPromotions
import com.nokaori.genshinaibuilder.data.remote.mapper.mapWeaponRefinements
import com.nokaori.genshinaibuilder.data.remote.mapper.toSetEntity
import com.nokaori.genshinaibuilder.data.remote.mapper.mapRelicPieces
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import kotlinx.coroutines.delay

class GameDataRepositoryImpl(
    private val characterDao: CharacterDao,
    private val statCurveDao: StatCurveDao,
    private val weaponDao: WeaponDao,
    private val artifactDao: ArtifactDao,
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
            val dtoList = listResponse.data.items.values.toList()
            
            val basicEntities = dtoList.map { it.toEntity() }
            
            val entityMap = dtoList.zip(basicEntities).associate { (dto, entity) -> dto.id to entity }

            characterDao.insertCharacters(basicEntities)
            Log.d("GameDataRepo", "Saved ${basicEntities.size} basic characters.")

            var processedCount = 0
            
            for (dto in dtoList) {
                val safeId = dto.id ?: continue
                
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

            Log.d("GameDataRepo", "Fetching weapon list...")
            val weaponListResponse = api.getWeaponList()
            val weaponDtoList = weaponListResponse.data.items.values.toList()
                .filter { it.isWeaponSkin != true }

            val basicWeaponEntities = weaponDtoList.map { it.toEntity() }
            val weaponEntityMap = weaponDtoList.zip(basicWeaponEntities).associate { (dto, entity) -> dto.id to entity }

            weaponDao.insertWeapons(basicWeaponEntities)
            Log.d("GameDataRepo", "Saved ${basicWeaponEntities.size} basic weapons.")

            var processedWeaponCount = 0
            for (dto in weaponDtoList) {
                val safeId = dto.id ?: continue
                try {
                    val detailResponse = api.getWeaponDetail(safeId)
                    val detailDto = detailResponse.data
                    
                    val currentWeaponEntity = weaponEntityMap[safeId]
                    if (currentWeaponEntity != null) {
                        val updatedWeaponEntity = currentWeaponEntity.updateWithDetails(detailDto)
                        weaponDao.insertWeapons(listOf(updatedWeaponEntity))

                        val weaponId = updatedWeaponEntity.id

                        val refinement = mapWeaponRefinements(weaponId, detailDto)
                        if (refinement != null) {
                            weaponDao.insertRefinements(listOf(refinement))
                        }

                        val promotions = mapWeaponPromotions(weaponId, detailDto)
                        weaponDao.insertPromotions(promotions)
                    }
                    processedWeaponCount++
                    if (processedWeaponCount % 20 == 0) Log.d("GameDataRepo", "Updated $processedWeaponCount weapons...")
                    delay(50)
                } catch (e: Exception) {
                    Log.e("GameDataRepo", "Failed to update detail for weapon ${dto.name} ($safeId)", e)
                }
            }

            Log.d("GameDataRepo", "Weapon Update Complete!")

            Log.d("GameDataRepo", "Fetching artifact list...")
            val relicListResponse = api.getRelicList()
            val relicDtoList = relicListResponse.data.items.values.toList()
            
            var processedRelicCount = 0
            
            for (listItem in relicDtoList) {
                try {
                    // Качаем детали для каждого сета, чтобы получить куски (suit)
                    val detailResponse = api.getRelicDetail(listItem.id)
                    val detailDto = detailResponse.data
                    
                    // 1. Сохраняем Сет
                    val setEntity = detailDto.toSetEntity()
                    artifactDao.insertArtifactSets(listOf(setEntity))
                    
                    // 2. Сохраняем Куски
                    val pieces = mapRelicPieces(setEntity.id, detailDto)
                    artifactDao.insertArtifactPieces(pieces)
                    
                    processedRelicCount++
                    if (processedRelicCount % 20 == 0) Log.d("GameDataRepo", "Updated $processedRelicCount relic sets...")
                    delay(50)
                    
                } catch (e: Exception) {
                     Log.e("GameDataRepo", "Failed to update relic set ${listItem.name}", e)
                }
            }
            Log.d("GameDataRepo", "Artifact Update Complete!")

            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e("GameDataRepo", "Global Update Error", e)
            Result.failure(e)
        }
    }
}