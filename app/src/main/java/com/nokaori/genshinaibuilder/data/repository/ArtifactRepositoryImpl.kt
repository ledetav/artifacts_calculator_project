package com.nokaori.genshinaibuilder.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nokaori.genshinaibuilder.data.local.dao.ArtifactDao
import com.nokaori.genshinaibuilder.data.local.dao.StatCurveDao
import com.nokaori.genshinaibuilder.data.local.dao.UserDao
import com.nokaori.genshinaibuilder.data.local.entity.UserArtifactEntity
import com.nokaori.genshinaibuilder.data.mapper.toDomain
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactPiece
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.StatCurve
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.SupportedLanguages
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.PieceMatchInfo
import com.nokaori.genshinaibuilder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArtifactRepositoryImpl @Inject constructor (
    private val artifactDao: ArtifactDao,
    private val userDao: UserDao,
    private val statCurveDao: StatCurveDao,
    private val themeRepository: SettingsRepository
) : ArtifactRepository {
    private val defaultLanguage = SupportedLanguages.EN

    override fun getArtifacts(): Flow<List<Artifact>> {
        return userDao.getUserArtifactsComplete().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAvailableArtifactSetsPaged(): Flow<PagingData<ArtifactSet>> {
        return themeRepository.appLanguage.flatMapLatest { language ->
            Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = { artifactDao.getAllArtifactSetsPaging(language) }
            ).flow.map { pagingData ->
                pagingData.map { entity ->
                    entity.toDomain()
                }
            }
        }
    }

    override suspend fun getAllArtifactUrls(): List<String> {
        val language = themeRepository.appLanguage.first()
        return artifactDao.getAllArtifactUrls(language)
    }

    override fun getAvailableArtifactSets(): Flow<List<ArtifactSet>> {
        return themeRepository.appLanguage.flatMapLatest { language ->
            artifactDao.getAllArtifactSets(language).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    override suspend fun addArtifact(artifact: Artifact) {
        val language = themeRepository.appLanguage.first()
        val setEntity = artifactDao.getSetByName(language, artifact.setName)
            ?: throw IllegalArgumentException("Set '${artifact.setName}' not found")

        val mainStatVal = when (val v = artifact.mainStat.value) {
            is com.nokaori.genshinaibuilder.domain.model.StatValue.IntValue -> v.value.toFloat()
            is com.nokaori.genshinaibuilder.domain.model.StatValue.DoubleValue -> v.value.toFloat()
        }

        val entity = UserArtifactEntity(
            id = 0,
            setId = setEntity.id,
            slot = artifact.slot,
            rarity = artifact.rarity.stars,
            level = artifact.level,
            isLocked = artifact.isLocked,
            mainStatType = artifact.mainStat.type,
            mainStatValue = mainStatVal,
            subStats = artifact.subStats,
            equippedCharacterId = null
        )

        userDao.insertUserArtifact(entity)
    }

    override suspend fun getArtifactSetDetails(setId: Int): ArtifactSet {
        val language = themeRepository.appLanguage.first()
        val setEntity = artifactDao.getArtifactSetById(setId, language)
            ?: throw IllegalStateException("Set not found")

        val piecesEntities = artifactDao.getPiecesBySetId(setId, language).first()

        return setEntity.toDomain(pieces = piecesEntities)
    }

    override suspend fun getArtifactMainStatCurve(rarity: Int, statType: StatType): StatCurve? {
        val propName = mapStatTypeToYattaString(statType) ?: return null
        val curveId = "ARTIFACT_RANK_${rarity}_MAIN_$propName"

        val entity = statCurveDao.getCurve(curveId) ?: return null
        return StatCurve(entity.id, entity.points)
    }

    private fun mapStatTypeToYattaString(type: StatType): String? {
        return when(type) {
            StatType.HP -> "FIGHT_PROP_HP"
            StatType.ATK -> "FIGHT_PROP_ATTACK"
            StatType.DEF -> "FIGHT_PROP_DEFENSE"
            StatType.HP_PERCENT -> "FIGHT_PROP_HP_PERCENT"
            StatType.ATK_PERCENT -> "FIGHT_PROP_ATTACK_PERCENT"
            StatType.DEF_PERCENT -> "FIGHT_PROP_DEFENSE_PERCENT"
            StatType.CRIT_RATE -> "FIGHT_PROP_CRITICAL"
            StatType.CRIT_DMG -> "FIGHT_PROP_CRITICAL_HURT"
            StatType.ENERGY_RECHARGE -> "FIGHT_PROP_CHARGE_EFFICIENCY"
            StatType.ELEMENTAL_MASTERY -> "FIGHT_PROP_ELEMENT_MASTERY"
            StatType.HEALING_BONUS -> "FIGHT_PROP_HEAL_ADD"
            StatType.PHYSICAL_DAMAGE_BONUS -> "FIGHT_PROP_PHYSICAL_ADD_HURT"
            StatType.PYRO_DAMAGE_BONUS -> "FIGHT_PROP_FIRE_ADD_HURT"
            StatType.HYDRO_DAMAGE_BONUS -> "FIGHT_PROP_WATER_ADD_HURT"
            StatType.CRYO_DAMAGE_BONUS -> "FIGHT_PROP_ICE_ADD_HURT"
            StatType.ELECTRO_DAMAGE_BONUS -> "FIGHT_PROP_ELEC_ADD_HURT"
            StatType.ANEMO_DAMAGE_BONUS -> "FIGHT_PROP_WIND_ADD_HURT"
            StatType.GEO_DAMAGE_BONUS -> "FIGHT_PROP_ROCK_ADD_HURT"
            StatType.DENDRO_DAMAGE_BONUS -> "FIGHT_PROP_GRASS_ADD_HURT"
            else -> null
        }
    }

    override suspend fun getArtifactSubStatRolls(rarity: Int, statType: StatType): List<Float>? {
        val propName = mapStatTypeToYattaString(statType) ?: return null
        val curveId = "ARTIFACT_RANK_${rarity}_SUB_$propName"

        val entity = statCurveDao.getCurve(curveId) ?: return null

        return entity.points.toSortedMap().values.toList()
    }

    override suspend fun getArtifactById(id: Int): Artifact? {
        return userDao.getUserArtifactCompleteById(id)?.toDomain()
    }

    override suspend fun updateArtifact(artifact: Artifact) {
        val language = themeRepository.appLanguage.first()
        val existingEntity = userDao.getUserArtifactById(artifact.id) ?: return
        
        val setEntity = artifactDao.getSetByName(language, artifact.setName)
            ?: throw IllegalArgumentException("Set '${artifact.setName}' not found")

        val mainStatVal = when (val v = artifact.mainStat.value) {
            is com.nokaori.genshinaibuilder.domain.model.StatValue.IntValue -> v.value.toFloat()
            is com.nokaori.genshinaibuilder.domain.model.StatValue.DoubleValue -> v.value.toFloat()
        }

        val entity = UserArtifactEntity(
            id = artifact.id,
            setId = setEntity.id,
            slot = artifact.slot,
            rarity = artifact.rarity.stars,
            level = artifact.level,
            isLocked = artifact.isLocked,
            mainStatType = artifact.mainStat.type,
            mainStatValue = mainStatVal,
            subStats = artifact.subStats,
            equippedCharacterId = existingEntity.equippedCharacterId
        )

        userDao.updateUserArtifact(entity)
    }

    override suspend fun getAllPiecesForMatching(): List<PieceMatchInfo> {
        return artifactDao.getAllArtifactPieces().map { entity ->
            PieceMatchInfo(
                setId = entity.setId,
                slot = entity.slot,
                name = entity.name
            )
        }
    }
}