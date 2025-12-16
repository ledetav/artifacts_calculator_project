package com.nokaori.genshinaibuilder.data.repository

import android.util.Log
import com.nokaori.genshinaibuilder.data.local.dao.*
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.remote.mapper.*
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GameDataRepositoryImpl(
    private val characterDao: CharacterDao,
    private val statCurveDao: StatCurveDao,
    private val weaponDao: WeaponDao,
    private val artifactDao: ArtifactDao,
    private val api: YattaApi
) : GameDataRepository {
    private sealed interface UpdateEvent {
        data class Log(val message: String) : UpdateEvent
        data class Error(val message: String, val throwable: Throwable? = null) : UpdateEvent
        // Можно добавить Progress(val current: Int, val total: Int), если захотим точный бар
    }

    override fun updateGameData(): Flow<SyncStatus> = flow {
        val eventChannel = Channel<UpdateEvent>(Channel.UNLIMITED)
        val logsList = mutableListOf<String>()

        coroutineScope {
            val collectorJob = launch {
                var lastEmitTime = 0L
                
                for (event in eventChannel) {
                    when (event) {
                        is UpdateEvent.Log -> {
                            logsList.add(event.message)
                            Log.d("Sync", event.message) 
                        }
                        is UpdateEvent.Error -> {
                            val msg = "❌ ${event.message}"
                            logsList.add(msg)
                            Log.e("Sync", msg, event.throwable)
                        }
                    }

                    val now = System.currentTimeMillis()
                    if (now - lastEmitTime > 50) {
                        emit(SyncStatus.InProgress(
                            message = logsList.lastOrNull() ?: "Загрузка...",
                            progress = 0.5f,
                            logs = logsList.toList()
                        ))
                        lastEmitTime = now
                    }
                }
            }

            try {
                val startTime = System.currentTimeMillis()
                
                eventChannel.send(UpdateEvent.Log("🚀 Начинаем обновление базы данных..."))

                updateStatCurves(eventChannel)

                val charsDeferred = async { updateCharacters(eventChannel) }
                val weaponsDeferred = async { updateWeapons(eventChannel) }
                val artifactsDeferred = async { updateArtifacts(eventChannel) }

                val newChars = charsDeferred.await()
                val newWeapons = weaponsDeferred.await()
                val newArts = artifactsDeferred.await()

                val totalNew = newChars + newWeapons + newArts
                val duration = (System.currentTimeMillis() - startTime) / 1000f
                
                val finalMsg = "✅ Успешно! Заняло: ${duration}с. Новых объектов: $totalNew"
                logsList.add(finalMsg)
                
                emit(SyncStatus.Success(finalMsg, logsList.toList()))

            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Unknown Error"
                logsList.add("🔥 КРИТИЧЕСКИЙ СБОЙ: $errorMsg")
                emit(SyncStatus.Error(errorMsg))
            } finally {
                eventChannel.close()
                collectorJob.join()
            }
        }
    }

    private suspend fun updateStatCurves(channel: SendChannel<UpdateEvent>) {
        channel.send(UpdateEvent.Log("📥 [Кривые] Скачивание..."))
        try {
            val curves = api.getAvatarCurves().toEntities() +
                         api.getWeaponCurves().toEntities() +
                         api.getRelicCurves().toEntities()
            statCurveDao.insertCurves(curves)
            channel.send(UpdateEvent.Log("✨ [Кривые] Сохранено ${curves.size} шт."))
        } catch (e: Exception) {
            channel.send(UpdateEvent.Error("Ошибка кривых", e))
            throw e // Кривые критичны, прерываем всё, если упали
        }
    }

    private suspend fun updateCharacters(channel: SendChannel<UpdateEvent>): Int {
        channel.send(UpdateEvent.Log("👤 [Персонажи] Получение списка..."))
        
        val listResponse = api.getAvatarList()
        val dtoList = listResponse.data.items.values
        val basicEntities = dtoList.map { it.toEntity() }
        val entityMap = basicEntities.associateBy { it.id }

        val existingIds = characterDao.getAllCharacterIds().toSet()
        val newCount = basicEntities.count { it.id !in existingIds }

        characterDao.insertCharacters(basicEntities)

        val chunks = dtoList.chunked(5) 
        var processed = 0

        chunks.forEach { batch ->
            coroutineScope {
                batch.map { dto ->
                    async {
                        val safeId = dto.id ?: return@async
                        try {
                            val details = api.getAvatarDetail(safeId).data
                            val entityId = dto.toEntity().id
                            val currentEntity = entityMap[entityId]
                            
                            if (currentEntity != null) {
                                val updated = currentEntity.updateWithDetails(details)
                                val (talents, consts) = mapTalentsAndConstellations(updated.id, details)
                                val promos = mapPromotions(updated.id, details)

                                characterDao.insertCharacters(listOf(updated))
                                characterDao.insertTalents(talents)
                                characterDao.insertConstellations(consts)
                                characterDao.insertPromotions(promos)
                                
                                // channel.send(UpdateEvent.Log("   -> Обновлен: ${updated.name}")) // Слишком много спама, если включить
                            }
                        } catch (e: Exception) {
                            channel.send(UpdateEvent.Error("Персонаж ${dto.name}", e))
                        }
                    }
                }.awaitAll()
            }
            processed += batch.size
            if (processed % 10 == 0) channel.send(UpdateEvent.Log("👤 [Персонажи] Обработано $processed/${dtoList.size}..."))
            delay(50)
        }
        
        channel.send(UpdateEvent.Log("✅ [Персонажи] Готово. Новых: $newCount"))
        return newCount
    }

    private suspend fun updateWeapons(channel: SendChannel<UpdateEvent>): Int {
        channel.send(UpdateEvent.Log("⚔️ [Оружие] Получение списка..."))
        val listResponse = api.getWeaponList()
        val dtoList = listResponse.data.items.values.filter { it.isWeaponSkin != true }
        
        val basicEntities = dtoList.map { it.toEntity() }
        val entityMap = basicEntities.associateBy { it.id }
        
        val existingIds = weaponDao.getAllWeaponIds().toSet()
        val newCount = basicEntities.count { it.id !in existingIds }

        weaponDao.insertWeapons(basicEntities)

        val chunks = dtoList.chunked(10)
        var processed = 0

        chunks.forEach { batch ->
            coroutineScope {
                batch.map { dto ->
                    async {
                        val safeId = dto.id ?: return@async
                        try {
                            val details = api.getWeaponDetail(safeId).data
                            val entityId = dto.toEntity().id
                            val currentEntity = entityMap[entityId]
                            
                            if (currentEntity != null) {
                                val updated = currentEntity.updateWithDetails(details)
                                val refine = mapWeaponRefinements(updated.id, details)
                                val promos = mapWeaponPromotions(updated.id, details)

                                weaponDao.insertWeapons(listOf(updated))
                                refine?.let { weaponDao.insertRefinements(listOf(it)) }
                                weaponDao.insertPromotions(promos)
                            }
                        } catch (e: Exception) {
                            channel.send(UpdateEvent.Error("Оружие ${dto.name}", e))
                        }
                    }
                }.awaitAll()
            }
            processed += batch.size
            if (processed % 20 == 0) channel.send(UpdateEvent.Log("⚔️ [Оружие] Обработано $processed/${dtoList.size}..."))
            delay(50)
        }
        channel.send(UpdateEvent.Log("✅ [Оружие] Готово. Новых: $newCount"))
        return newCount
    }

    private suspend fun updateArtifacts(channel: SendChannel<UpdateEvent>): Int {
        channel.send(UpdateEvent.Log("🏺 [Артефакты] Получение списка..."))
        val dtoList = api.getRelicList().data.items.values
        
        val existingIds = artifactDao.getAllArtifactSetIds().toSet()
        val newCount = dtoList.count { it.id !in existingIds }

        val chunks = dtoList.chunked(5)
        var processed = 0
        
        chunks.forEach { batch ->
            coroutineScope {
                batch.map { dto ->
                    async {
                        try {
                            val details = api.getRelicDetail(dto.id).data
                            val set = details.toSetEntity()
                            val pieces = mapRelicPieces(set.id, details)
                            
                            artifactDao.insertArtifactSets(listOf(set))
                            artifactDao.insertArtifactPieces(pieces)
                        } catch (e: Exception) {
                            channel.send(UpdateEvent.Error("Сет ${dto.name}", e))
                        }
                    }
                }.awaitAll()
            }
            processed += batch.size
            delay(50)
        }
        
        channel.send(UpdateEvent.Log("✅ [Артефакты] Готово. Новых: $newCount"))
        return newCount
    }
}