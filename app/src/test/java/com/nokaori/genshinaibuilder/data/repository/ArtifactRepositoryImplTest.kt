package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.ArtifactDao
import com.nokaori.genshinaibuilder.data.local.dao.StatCurveDao
import com.nokaori.genshinaibuilder.data.local.dao.UserDao
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactPieceEntity
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.data.local.entity.StatCurveEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserArtifactEntity
import com.nokaori.genshinaibuilder.data.local.model.UserArtifactComplete
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.StatValue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ArtifactRepositoryImplTest {
    @Mock
    private lateinit var artifactDao: ArtifactDao
    @Mock
    private lateinit var userDao: UserDao
    @Mock
    private lateinit var statCurveDao: StatCurveDao

    private lateinit var repository: ArtifactRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ArtifactRepositoryImpl(artifactDao, userDao, statCurveDao)
    }

    @Test
    fun getArtifacts_returnsFlowOfArtifacts() = runTest {
        val mockComplete = UserArtifactComplete(
            userArtifact = UserArtifactEntity(
                id = 1,
                setId = 1,
                slot = ArtifactSlot.FLOWER_OF_LIFE,
                rarity = 5,
                level = 20,
                isLocked = false,
                mainStatType = StatType.HP,
                mainStatValue = 4780f,
                subStats = emptyList(),
                equippedCharacterId = null
            ),
            setEntity = ArtifactSetEntity(
                id = 1,
                language = "en",
                name = "Crimson Witch",
                rarities = listOf(4, 5),
                bonus2pc = "Test",
                bonus4pc = "Test",
                iconUrl = "url"
            ),
            pieceEntity = ArtifactPieceEntity(
                id = 1,
                language = "en",
                setId = 1,
                slot = ArtifactSlot.FLOWER_OF_LIFE,
                name = "Flower",
                iconUrl = "url"
            )
        )

        whenever(userDao.getUserArtifactsComplete()).thenReturn(flowOf(listOf(mockComplete)))

        val result = mutableListOf<Artifact>()
        repository.getArtifacts().collect { result.addAll(it) }

        assertEquals(1, result.size)
    }

    @Test
    fun getAvailableArtifactSets_returnsFlowOfSets() = runTest {
        val mockSet = ArtifactSetEntity(
            id = 1,
            language = "en",
            name = "Crimson Witch",
            rarities = listOf(4, 5),
            bonus2pc = "Test",
            bonus4pc = "Test",
            iconUrl = "url"
        )

        whenever(artifactDao.getAllArtifactSets()).thenReturn(flowOf(listOf(mockSet)))

        val result = mutableListOf<ArtifactSet>()
        repository.getAvailableArtifactSets().collect { result.addAll(it) }

        assertEquals(1, result.size)
    }

    @Test
    fun getAllArtifactUrls_returnsListOfUrls() = runTest {
        val urls = listOf("url1", "url2", "url3")
        whenever(artifactDao.getAllArtifactUrls()).thenReturn(urls)

        val result = repository.getAllArtifactUrls()

        assertEquals(urls, result)
    }

    @Test
    fun addArtifact_withValidArtifact_insertsUserArtifact() = runTest {
        val artifact = Artifact(
            id = 0,
            setName = "Crimson Witch",
            slot = ArtifactSlot.FLOWER_OF_LIFE,
            rarity = Rarity.FIVE_STARS,
            level = 20,
            isLocked = false,
            artifactName = "Flower",
            mainStat = Stat(StatType.HP, StatValue.IntValue(4780)),
            subStats = emptyList()
        )

        val mockSet = ArtifactSetEntity(
            id = 1,
            language = "en",
            name = "Crimson Witch",
            rarities = listOf(4, 5),
            bonus2pc = "Test",
            bonus4pc = "Test",
            iconUrl = "url"
        )

        whenever(artifactDao.getSetByName("Crimson Witch")).thenReturn(mockSet)

        repository.addArtifact(artifact)

        verify(userDao).insertUserArtifact(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun addArtifact_withInvalidSetName_throwsException() = runTest {
        val artifact = Artifact(
            id = 0,
            setName = "NonExistent",
            slot = ArtifactSlot.FLOWER_OF_LIFE,
            rarity = Rarity.FIVE_STARS,
            level = 20,
            isLocked = false,
            artifactName = "Flower",
            mainStat = Stat(StatType.HP, StatValue.IntValue(4780)),
            subStats = emptyList()
        )

        whenever(artifactDao.getSetByName("NonExistent")).thenReturn(null)

        repository.addArtifact(artifact)
    }

    @Test
    fun getArtifactMainStatCurve_withValidRarityAndStat_returnsCurve() = runTest {
        val curve = StatCurveEntity(
            id = "ARTIFACT_RANK_5_MAIN_FIGHT_PROP_HP",
            points = mapOf(0 to 100f, 20 to 500f)
        )

        whenever(statCurveDao.getCurve("ARTIFACT_RANK_5_MAIN_FIGHT_PROP_HP")).thenReturn(curve)

        val result = repository.getArtifactMainStatCurve(5, StatType.HP)

        assertEquals(curve.id, result?.id)
    }

    @Test
    fun getArtifactMainStatCurve_withInvalidStat_returnsNull() = runTest {
        val result = repository.getArtifactMainStatCurve(5, StatType.UNKNOWN)

        assertNull(result)
    }

    @Test
    fun getArtifactMainStatCurve_withNonExistentCurve_returnsNull() = runTest {
        whenever(statCurveDao.getCurve("ARTIFACT_RANK_5_MAIN_FIGHT_PROP_HP")).thenReturn(null)

        val result = repository.getArtifactMainStatCurve(5, StatType.HP)

        assertNull(result)
    }

    @Test
    fun getArtifactSubStatRolls_withValidRarityAndStat_returnsRolls() = runTest {
        val curve = StatCurveEntity(
            id = "ARTIFACT_RANK_5_SUB_FIGHT_PROP_ATTACK_PERCENT",
            points = mapOf(0 to 3.9f, 1 to 7.8f, 2 to 11.7f)
        )

        whenever(statCurveDao.getCurve("ARTIFACT_RANK_5_SUB_FIGHT_PROP_ATTACK_PERCENT")).thenReturn(curve)

        val result = repository.getArtifactSubStatRolls(5, StatType.ATK_PERCENT)

        assertEquals(3, result?.size)
    }

    @Test
    fun getArtifactSubStatRolls_withInvalidStat_returnsNull() = runTest {
        val result = repository.getArtifactSubStatRolls(5, StatType.UNKNOWN)

        assertNull(result)
    }

    @Test
    fun getArtifactSubStatRolls_withNonExistentCurve_returnsNull() = runTest {
        whenever(statCurveDao.getCurve("ARTIFACT_RANK_5_SUB_FIGHT_PROP_ATTACK_PERCENT")).thenReturn(null)

        val result = repository.getArtifactSubStatRolls(5, StatType.ATK_PERCENT)

        assertNull(result)
    }
}
