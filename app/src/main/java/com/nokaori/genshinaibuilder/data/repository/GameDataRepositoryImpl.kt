package com.nokaori.genshinaibuilder.data.repository

import android.util.Log
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.data.local.dao.*
import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.data.remote.mapper.*
import com.nokaori.genshinaibuilder.domain.model.SupportedLanguages
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.model.UiText
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import java.util.Collections
import javax.inject.Inject
import com.nokaori.genshinaibuilder.domain.repository.SettingsRepository

class GameDataRepositoryImpl @Inject constructor(
    private val characterDao: CharacterDao,
    private val statCurveDao: StatCurveDao,
    private val weaponDao: WeaponDao,
    private val artifactDao: ArtifactDao,
    private val api: YattaApi,
    private val themeRepository: SettingsRepository
) : GameDataRepository {

    override fun updateGameData(): Flow<SyncStatus> = channelFlow {
        val logs = Collections.synchronizedList(mutableListOf<UiText>())

        fun log(msg: UiText, progress: Float = 0f) {
            logs.add(msg)
            trySend(SyncStatus.InProgress(msg, progress, logs.toList()))
        }

        try {
            val startTime = System.currentTimeMillis()
            log(UiText.StringResource(R.string.sync_log_curves_start), 0.05f)
            val curves = api.getAvatarCurves().toEntities() +
                    api.getWeaponCurves().toEntities() +
                    api.getRelicCurves().toEntities()
            statCurveDao.insertCurves(curves)
            log(UiText.StringResource(R.string.sync_log_curves_done, curves.size), 0.1f)

            log(UiText.StringResource(R.string.sync_log_parallel_start), 0.1f)

            val currentLanguage = themeRepository.appLanguage.first()
            val totalNew = updateCharacters(currentLanguage, ::log) +
                    updateWeapons(currentLanguage, ::log) +
                    updateArtifacts(currentLanguage, ::log)

            val duration = (System.currentTimeMillis() - startTime) / 1000f
            val finalMsg = UiText.StringResource(R.string.sync_log_success, duration, totalNew)
            logs.add(finalMsg)

            send(SyncStatus.Success(finalMsg, logs.toList()))

        } catch (e: Exception) {
            val msg = e.localizedMessage?.let { UiText.DynamicString(it) } 
                ?: UiText.StringResource(R.string.sync_error_unknown)
            Log.e("GameDataRepo", "Error", e)
            send(SyncStatus.Error(msg))
        }
    }

    private suspend fun updateCharacters(language: String, onLog: (UiText, Float) -> Unit): Int {
        onLog(UiText.DynamicString("[$language] Loading characters..."), 0f)

        val listResponse = api.getAvatarList(language)
        val dtoList = listResponse.data.items.values
        val basicEntities = dtoList.map { it.toEntity(language) }
        val entityMap = dtoList.zip(basicEntities).associate { (dto, entity) -> dto.id!! to entity }

        val existingIds = characterDao.getAllCharacterIds().toSet()
        val newCount = basicEntities.count { it.id !in existingIds }

        characterDao.insertCharacters(basicEntities)

        val chunks = dtoList.chunked(5)
        var processed = 0

        chunks.forEach { batch ->
            coroutineScope {
                batch.map { dto ->
                    async {
                        val id = dto.id ?: return@async
                        try {
                            val details = api.getAvatarDetail(language, id).data
                            val current = entityMap[id] as? CharacterEntity
                            if (current != null) {
                                val updated = current.updateWithDetails(details)
                                val (talents, consts) = mapTalentsAndConstellations(updated.id, language, details)
                                val promo = mapPromotions(updated.id, language, details)

                                characterDao.insertCharacters(listOf(updated))
                                characterDao.insertTalents(talents)
                                characterDao.insertConstellations(consts)
                                characterDao.insertPromotions(promo)
                            }
                        } catch (e: Exception) {
                            onLog(UiText.DynamicString("[$language] Error loading ${dto.name ?: "Unknown"}"), 0f)
                        }
                    }
                }.awaitAll()
            }
            processed += batch.size
            if (processed % 10 == 0) onLog(UiText.DynamicString("[$language] Characters: $processed/${dtoList.size}"), 0f)
            delay(50)
        }

        onLog(UiText.DynamicString("[$language] Characters done: $newCount new"), 0f)
        return newCount
    }

    private suspend fun updateWeapons(language: String, onLog: (UiText, Float) -> Unit): Int {
        onLog(UiText.DynamicString("[$language] Loading weapons..."), 0f)

        val listResponse = api.getWeaponList(language)
        val dtoList = listResponse.data.items.values.filter { it.isWeaponSkin != true }
        val basicEntities = dtoList.map { it.toEntity(language) }
        val entityMap = dtoList.zip(basicEntities).associate { (dto, entity) -> dto.id!! to entity }

        val existingIds = weaponDao.getAllWeaponIds().toSet()
        val newCount = basicEntities.count { it.id !in existingIds }

        weaponDao.insertWeapons(basicEntities)

        val chunks = dtoList.chunked(10)
        var processed = 0

        chunks.forEach { batch ->
            coroutineScope {
                batch.map { dto ->
                    async {
                        val id = dto.id ?: return@async
                        try {
                            val details = api.getWeaponDetail(language, id).data
                            val current = entityMap[id] as? WeaponEntity
                            if (current != null) {
                                val updated = current.updateWithDetails(details)
                                val refine = mapWeaponRefinements(updated.id, language, details)
                                val promo = mapWeaponPromotions(updated.id, language, details)

                                weaponDao.insertWeapons(listOf(updated))
                                refine?.let { weaponDao.insertRefinements(listOf(it)) }
                                weaponDao.insertPromotions(promo)
                            }
                        } catch (e: Exception) { }
                    }
                }.awaitAll()
            }
            processed += batch.size
            if (processed % 20 == 0) onLog(UiText.DynamicString("[$language] Weapons: $processed/${dtoList.size}"), 0f)
            delay(50)
        }

        onLog(UiText.DynamicString("[$language] Weapons done: $newCount new"), 0f)
        return newCount
    }

    private suspend fun updateArtifacts(language: String, onLog: (UiText, Float) -> Unit): Int {
        onLog(UiText.DynamicString("[$language] Loading artifacts..."), 0f)
        val dtoList = api.getRelicList(language).data.items.values

        val existingIds = artifactDao.getAllArtifactSetIds().toSet()
        val newCount = dtoList.count { it.id !in existingIds }

        val chunks = dtoList.chunked(5)

        chunks.forEach { batch ->
            coroutineScope {
                batch.map { dto ->
                    async {
                        try {
                            val details = api.getRelicDetail(language, dto.id).data
                            val set = details.toSetEntity(language)
                            val pieces = mapRelicPieces(set.id, language, details)

                            artifactDao.insertArtifactSets(listOf(set))
                            artifactDao.insertArtifactPieces(pieces)
                        } catch (e: Exception) { }
                    }
                }.awaitAll()
            }
            delay(50)
        }

        onLog(UiText.DynamicString("[$language] Artifacts done: $newCount new"), 0f)
        return newCount
    }
}