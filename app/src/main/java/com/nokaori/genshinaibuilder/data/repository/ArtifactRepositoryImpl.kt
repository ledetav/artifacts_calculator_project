package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.ArtifactDao
import com.nokaori.genshinaibuilder.data.local.dao.UserDao
import com.nokaori.genshinaibuilder.data.local.entity.UserArtifactEntity
import com.nokaori.genshinaibuilder.data.mapper.toDomain
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArtifactRepositoryImpl(
    private val artifactDao: ArtifactDao, // Энциклопедия
    private val userDao: UserDao          // Инвентарь
) : ArtifactRepository {

    // Получаем артефакты пользователя + данные об их сетах
    override fun getArtifacts(): Flow<List<Artifact>> {
        return userDao.getUserArtifactsComplete().map { list ->
            list.map { item ->
                // Используем маппер
                item.toDomain()
            }
        }
    }

    // Список доступных сетов (из энциклопедии)
    override fun getAvailableArtifactSets(): Flow<List<ArtifactSet>> {
        return artifactDao.getAllArtifactSets().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addArtifact(artifact: Artifact) {
        // 1. Ищем ID сета по названию
        // Если сет не найден (например, ошибка OCR или бага), кидаем ошибку или игнорируем
        val setEntity = artifactDao.getSetByName(artifact.setName)
            ?: throw IllegalArgumentException("Set with name '${artifact.setName}' not found in database")

        // 2. Извлекаем числовое значение главного стата
        val mainStatValueFloat = when (val v = artifact.mainStat.value) {
            is com.nokaori.genshinaibuilder.domain.model.StatValue.IntValue -> v.value.toFloat()
            is com.nokaori.genshinaibuilder.domain.model.StatValue.DoubleValue -> v.value.toFloat()
        }

        // 3. Создаем Entity для сохранения
        val newEntity = UserArtifactEntity(
            id = 0, // 0 означает, что Room сам сгенерирует новый ID
            setId = setEntity.id, // Используем найденный ID
            slot = artifact.slot,
            rarity = artifact.rarity.stars,
            level = artifact.level,
            isLocked = artifact.isLocked,
            mainStatType = artifact.mainStat.type,
            mainStatValue = mainStatValueFloat,
            subStats = artifact.subStats, // Конвертер Gson сделает из этого JSON строку
            equippedCharacterId = null // Свежесозданный артефакт никому не принадлежит
        )
        userDao.insertUserArtifact(newEntity)
    }
}