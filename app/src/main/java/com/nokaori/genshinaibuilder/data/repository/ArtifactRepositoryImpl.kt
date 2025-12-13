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
    private val artifactDao: ArtifactDao,
    private val userDao: UserDao
) : ArtifactRepository {

    override fun getArtifacts(): Flow<List<Artifact>> {
        return userDao.getUserArtifactsComplete().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAvailableArtifactSets(): Flow<List<ArtifactSet>> {
        return artifactDao.getAllArtifactSets().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addArtifact(artifact: Artifact) {
        // Ищем ID сета по имени
        val setEntity = artifactDao.getSetByName(artifact.setName)
            ?: throw IllegalArgumentException("Set '${artifact.setName}' not found")

        // Приводим значение стата к Float для БД
        val mainStatVal = when (val v = artifact.mainStat.value) {
            is com.nokaori.genshinaibuilder.domain.model.StatValue.IntValue -> v.value.toFloat()
            is com.nokaori.genshinaibuilder.domain.model.StatValue.DoubleValue -> v.value.toFloat()
        }

        // Создаем Entity
        val entity = UserArtifactEntity(
            id = 0,
            setId = setEntity.id,
            slot = artifact.slot,
            rarity = artifact.rarity.stars,
            level = artifact.level,
            isLocked = artifact.isLocked,
            mainStatType = artifact.mainStat.type,
            mainStatValue = mainStatVal,
            subStats = artifact.subStats, // Конвертер сделает JSON
            equippedCharacterId = null
        )

        userDao.insertUserArtifact(entity)
    }
}