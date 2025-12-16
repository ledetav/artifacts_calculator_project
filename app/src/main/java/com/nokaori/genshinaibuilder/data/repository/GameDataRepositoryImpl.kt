package com.nokaori.genshinaibuilder.data.repository

import android.util.Log
import com.nokaori.genshinaibuilder.data.local.dao.*
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.remote.mapper.*
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Collections

class GameDataRepositoryImpl(
    private val characterDao: CharacterDao,
    private val statCurveDao: StatCurveDao,
    private val weaponDao: WeaponDao,
    private val artifactDao: ArtifactDao,
    private val api: YattaApi
) : GameDataRepository {

    override fun updateGameData(): Flow<SyncStatus> = flow {
        val logs = Collections.synchronizedList(mutableListOf<String>())
        
        fun addLog(msg: String) {
            logs.add(msg)
            Log.d("Sync", msg)
        }

        emit(SyncStatus.InProgress("Подготовка...", 0f, logs.toList()))

        try {
            val startTime = System.currentTimeMillis()
            var newItemsTotal = 0

            addLog("📥 Загрузка математических кривых...")
            val curves = api.getAvatarCurves().toEntities() +
                         api.getWeaponCurves().toEntities() +
                         api.getRelicCurves().toEntities()
            statCurveDao.insertCurves(curves)
            addLog("✅ Кривые обновлены: ${curves.size} шт.")
            emit(SyncStatus.InProgress("Кривые готовы", 0.1f, logs.toList()))

            // --- 2. ПЕРСОНАЖИ ---
            addLog("📥 Загрузка списка персонажей...")
            val charListResponse = api.getAvatarList()
            val charDtos = charListResponse.data.items.values
            
            val existingCharIds = characterDao.getAllCharacterIds().toSet()
            val incomingCharIds = charDtos.mapNotNull { it.id?.let { id -> parseIdForCheck(id) } }.toSet()
            val newCharIds = incomingCharIds - existingCharIds
            
            addLog("📊 Персонажей: ${charDtos.size}. Новых: ${newCharIds.size}")

            val basicChars = charDtos.map { it.toEntity() }
            val charEntityMap = charDtos.associateBy({ it.id }, { basicChars[charDtos.indexOf(it)] })
            characterDao.insertCharacters(basicChars)

            val charChunks = charDtos.chunked(5) 
            val totalChunks = charChunks.size

            charChunks.forEachIndexed { index, batch ->
                val deferreds = batch.map { dto ->
                    coroutineScope {
                        async {
                            val safeId = dto.id ?: return@async null
                            try {
                                val details = api.getAvatarDetail(safeId).data
                                val entity = charEntityMap[safeId] ?: return@async null
                                
                                val updated = entity.updateWithDetails(details)
                                val (talents, consts) = mapTalentsAndConstellations(updated.id, details)
                                val promos = mapPromotions(updated.id, details)

                                Triple(updated, Pair(talents, consts), promos)
                            } catch (e: Exception) {
                                addLog("⚠️ Ошибка: ${dto.name} - ${e.message}")
                                null
                            }
                        }
                    }
                }
                val results = deferreds.awaitAll().filterNotNull()

                results.forEach { (char, tc, promo) ->
                    characterDao.insertCharacters(listOf(char))
                    characterDao.insertTalents(tc.first)
                    characterDao.insertConstellations(tc.second)
                    characterDao.insertPromotions(promo)
                }

                val progress = 0.1f + (0.4f * (index + 1) / totalChunks)
                emit(SyncStatus.InProgress("Персонажи: ${index * 5 + results.size}/${charDtos.size}", progress, logs.toList()))
                
                delay(100) 
            }
            newItemsTotal += newCharIds.size
            addLog("✅ Персонажи обновлены")


            // --- 3. ОРУЖИЕ ---
            addLog("📥 Загрузка списка оружия...")
            val weaponListResp = api.getWeaponList()
            val weaponDtos = weaponListResp.data.items.values.filter { it.isWeaponSkin != true }
            
            val existingWeaponIds = weaponDao.getAllWeaponIds().toSet()
            val newWeaponCount = weaponDtos.count { (it.id?.toIntOrNull() ?: 0) !in existingWeaponIds }
            
            addLog("📊 Оружия: ${weaponDtos.size}. Новых: $newWeaponCount")
            
            val basicWeapons = weaponDtos.map { it.toEntity() }
            val weaponMap = weaponDtos.associateBy({ it.id }, { basicWeapons[weaponDtos.indexOf(it)] })
            weaponDao.insertWeapons(basicWeapons)

            val weaponChunks = weaponDtos.chunked(10)
            val totalWeaponChunks = weaponChunks.size

            weaponChunks.forEachIndexed { index, batch ->
                val deferreds = batch.map { dto ->
                    coroutineScope {
                        async {
                            val id = dto.id ?: return@async null
                            try {
                                val details = api.getWeaponDetail(id).data
                                val entity = weaponMap[id] ?: return@async null
                                
                                val updated = entity.updateWithDetails(details)
                                val refine = mapWeaponRefinements(updated.id, details)
                                val promo = mapWeaponPromotions(updated.id, details)
                                
                                Triple(updated, refine, promo)
                            } catch (e: Exception) {
                                addLog("⚠️ Ошибка оружия: ${dto.name}")
                                null
                            }
                        }
                    }
                }
                
                val results = deferreds.awaitAll().filterNotNull()
                
                results.forEach { (w, r, p) ->
                    weaponDao.insertWeapons(listOf(w))
                    r?.let { weaponDao.insertRefinements(listOf(it)) }
                    weaponDao.insertPromotions(p)
                }

                val progress = 0.5f + (0.3f * (index + 1) / totalWeaponChunks)
                emit(SyncStatus.InProgress("Оружие: ${index * 10 + results.size}/${weaponDtos.size}", progress, logs.toList()))
                delay(50)
            }
            newItemsTotal += newWeaponCount
            addLog("✅ Оружие обновлено")


            // --- 4. АРТЕФАКТЫ ---
            addLog("📥 Загрузка артефактов...")
            val relicList = api.getRelicList().data.items.values
            
            val existingSets = artifactDao.getAllArtifactSetIds().toSet()
            val newRelics = relicList.count { it.id !in existingSets }
            addLog("📊 Сетов: ${relicList.size}. Новых: $newRelics")

            val relicResults = relicList.map { dto ->
                coroutineScope {
                    async {
                        try {
                            val details = api.getRelicDetail(dto.id).data
                            val set = details.toSetEntity()
                            val pieces = mapRelicPieces(set.id, details)
                            Pair(set, pieces)
                        } catch (e: Exception) {
                            addLog("⚠️ Ошибка сета: ${dto.name}")
                            null
                        }
                    }
                }
            }.awaitAll().filterNotNull()

            relicResults.forEach { (set, pieces) ->
                artifactDao.insertArtifactSets(listOf(set))
                artifactDao.insertArtifactPieces(pieces)
            }
            newItemsTotal += newRelics
            addLog("✅ Артефакты обновлены")

            // --- ФИНАЛ ---
            val duration = (System.currentTimeMillis() - startTime) / 1000
            val finalMsg = "Успешно! Заняло: ${duration} сек. Новых предметов: $newItemsTotal"
            addLog(finalMsg)
            
            emit(SyncStatus.Success(finalMsg, logs.toList()))

        } catch (e: Exception) {
            addLog("❌ КРИТИЧЕСКАЯ ОШИБКА: ${e.message}")
            emit(SyncStatus.Error(e.localizedMessage ?: "Unknown Error"))
        }
    }

    private fun parseIdForCheck(rawId: String): Int {
         val simple = rawId.toIntOrNull()
         if (simple != null) return simple
         val base = rawId.split("-")[0].toIntOrNull() ?: 0
         return base * 100
    }
}