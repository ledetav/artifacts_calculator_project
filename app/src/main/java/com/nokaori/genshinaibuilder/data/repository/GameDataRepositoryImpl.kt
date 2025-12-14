package com.nokaori.genshinaibuilder.data.repository

import android.util.Log
import com.nokaori.genshinaibuilder.data.local.dao.ArtifactDao
import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.local.dao.StatCurveDao
import com.nokaori.genshinaibuilder.data.local.dao.WeaponDao
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.remote.mapper.*
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import kotlinx.coroutines.delay

class GameDataRepositoryImpl(
    private val characterDao: CharacterDao,
    private val statCurveDao: StatCurveDao,
    private val weaponDao: WeaponDao,
    private val artifactDao: ArtifactDao,
    private val api: YattaApi
) : GameDataRepository {

    override suspend fun updateGameData(): Result<Unit> {
        return try {
            val startTime = System.currentTimeMillis()
            updateStatCurves()
            updateCharacters()
            updateWeapons()
            updateArtifacts()

            val duration = (System.currentTimeMillis() - startTime) / 1000
            Log.d("GameDataRepo", "GLOBAL UPDATE COMPLETE in ${duration}s")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("GameDataRepo", "Global Update Failed", e)
            Result.failure(e)
        }
    }
    
    private suspend fun updateStatCurves() {
        Log.d("GameDataRepo", "[1/4] Fetching Stat Curves...")
        
        val avatarCurves = api.getAvatarCurves().toEntities()
        val weaponCurves = api.getWeaponCurves().toEntities()
        val relicCurves = api.getRelicCurves().toEntities()

        statCurveDao.insertCurves(avatarCurves + weaponCurves + relicCurves)
        Log.d("GameDataRepo", " Saved ${avatarCurves.size + weaponCurves.size + relicCurves.size} curves.")
    }

    private suspend fun updateCharacters() {
        Log.d("GameDataRepo", "[2/4] Fetching Characters...")
        
        val listResponse = api.getAvatarList()
        val dtoList = listResponse.data.items.values.toList()
        val basicEntities = dtoList.map { it.toEntity() }
        val entityMap = dtoList.zip(basicEntities).associate { (dto, entity) -> dto.id to entity }

        characterDao.insertCharacters(basicEntities)
        Log.d("GameDataRepo", "   Saved ${basicEntities.size} basic characters. Fetching details...")

        var count = 0
        dtoList.forEach { dto ->
            val safeId = dto.id ?: return@forEach
            try {
                val detailResponse = api.getAvatarDetail(safeId)
                val detailDto = detailResponse.data
                val currentEntity = entityMap[safeId]

                if (currentEntity != null) {
                    val updatedEntity = currentEntity.updateWithDetails(detailDto)
                    characterDao.insertCharacters(listOf(updatedEntity))

                    val (talents, constellations) = mapTalentsAndConstellations(updatedEntity.id, detailDto)
                    characterDao.insertTalents(talents)
                    characterDao.insertConstellations(constellations)

                    val promotions = mapPromotions(updatedEntity.id, detailDto)
                    characterDao.insertPromotions(promotions)
                }
                
                count++
                if (count % 10 == 0) Log.d("GameDataRepo", "   Updated $count characters...")
                delay(50) // Anti-ban
            } catch (e: Exception) {
                Log.e("GameDataRepo", "    Failed char detail: ${dto.name}", e)
            }
        }
    }

    private suspend fun updateWeapons() {
        Log.d("GameDataRepo", "📥 [3/4] Fetching Weapons...")
        
        val listResponse = api.getWeaponList()
        val dtoList = listResponse.data.items.values.toList().filter { it.isWeaponSkin != true }
        
        val basicEntities = dtoList.map { it.toEntity() }
        val entityMap = dtoList.zip(basicEntities).associate { (dto, entity) -> dto.id to entity }

        weaponDao.insertWeapons(basicEntities)
        Log.d("GameDataRepo", "   Saved ${basicEntities.size} basic weapons. Fetching details...")

        var count = 0
        dtoList.forEach { dto ->
            val safeId = dto.id ?: return@forEach
            try {
                val detailResponse = api.getWeaponDetail(safeId)
                val detailDto = detailResponse.data
                val currentEntity = entityMap[safeId]

                if (currentEntity != null) {
                    val updatedEntity = currentEntity.updateWithDetails(detailDto)
                    weaponDao.insertWeapons(listOf(updatedEntity))

                    mapWeaponRefinements(updatedEntity.id, detailDto)?.let {
                        weaponDao.insertRefinements(listOf(it))
                    }

                    val promotions = mapWeaponPromotions(updatedEntity.id, detailDto)
                    weaponDao.insertPromotions(promotions)
                }
                
                count++
                if (count % 20 == 0) Log.d("GameDataRepo", "   Updated $count weapons...")
                delay(50)
            } catch (e: Exception) {
                Log.e("GameDataRepo", "    Failed weapon detail: ${dto.name}", e)
            }
        }
    }

    private suspend fun updateArtifacts() {
        Log.d("GameDataRepo", "[4/4] Fetching Artifacts...")
        
        val listResponse = api.getRelicList()
        val dtoList = listResponse.data.items.values
        
        var count = 0
        dtoList.forEach { listItem ->
            try {
                // Для артефактов сразу качаем детали, так как в списке нет инфы о кусках
                val detailResponse = api.getRelicDetail(listItem.id)
                val detailDto = detailResponse.data

                val setEntity = detailDto.toSetEntity()
                artifactDao.insertArtifactSets(listOf(setEntity))

                val pieces = mapRelicPieces(setEntity.id, detailDto)
                artifactDao.insertArtifactPieces(pieces)

                count++
                if (count % 20 == 0) Log.d("GameDataRepo", "   Updated $count sets...")
                delay(50)
            } catch (e: Exception) {
                Log.e("GameDataRepo", "    Failed relic set: ${listItem.name}", e)
            }
        }
    }
}