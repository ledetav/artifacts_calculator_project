package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.StatValue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterArtifactsUseCaseTest {
    private lateinit var useCase: FilterArtifactsUseCase
    private lateinit var testArtifacts: List<Artifact>

    @Before
    fun setup() {
        useCase = FilterArtifactsUseCase()
        testArtifacts = listOf(
            Artifact(
                id = 1,
                slot = ArtifactSlot.FLOWER_OF_LIFE,
                rarity = Rarity.FIVE_STARS,
                setName = "Crimson Witch",
                artifactName = "Crimson Witch Flower",
                level = 20,
                mainStat = Stat(StatType.HP, StatValue.IntValue(4780)),
                subStats = emptyList()
            ),
            Artifact(
                id = 2,
                slot = ArtifactSlot.PLUME_OF_DEATH,
                rarity = Rarity.FOUR_STARS,
                setName = "Noblesse Oblige",
                artifactName = "Noblesse Plume",
                level = 16,
                mainStat = Stat(StatType.ATK, StatValue.IntValue(311)),
                subStats = emptyList()
            ),
            Artifact(
                id = 3,
                slot = ArtifactSlot.SANDS_OF_EON,
                rarity = Rarity.FIVE_STARS,
                setName = "Crimson Witch",
                artifactName = "Crimson Witch Sands",
                level = 20,
                mainStat = Stat(StatType.ATK_PERCENT, StatValue.DoubleValue(0.466)),
                subStats = emptyList()
            )
        )
    }

    @Test
    fun invoke_withEmptySearchQuery_returnsAllArtifacts() {
        val result = useCase(
            testArtifacts,
            "",
            null,
            0f..20f,
            emptySet(),
            null
        )
        assertEquals(3, result.size)
    }

    @Test
    fun invoke_withSearchQuery_filtersArtifacts() {
        val result = useCase(
            testArtifacts,
            "Crimson",
            null,
            0f..20f,
            emptySet(),
            null
        )
        assertEquals(2, result.size)
    }

    @Test
    fun invoke_withSelectedSet_filtersBySet() {
        val set = ArtifactSet(id = 1, name = "Crimson Witch", iconUrl = "")
        val result = useCase(
            testArtifacts,
            "",
            set,
            0f..20f,
            emptySet(),
            null
        )
        assertEquals(2, result.size)
    }

    @Test
    fun invoke_withLevelRange_filtersByLevel() {
        val result = useCase(
            testArtifacts,
            "",
            null,
            16f..16f,
            emptySet(),
            null
        )
        assertEquals(1, result.size)
    }

    @Test
    fun invoke_withSelectedSlots_filtersBySlot() {
        val result = useCase(
            testArtifacts,
            "",
            null,
            0f..20f,
            setOf(ArtifactSlot.FLOWER_OF_LIFE, ArtifactSlot.PLUME_OF_DEATH),
            null
        )
        assertEquals(2, result.size)
    }

    @Test
    fun invoke_withSelectedMainStat_filtersByMainStat() {
        val result = useCase(
            testArtifacts,
            "",
            null,
            0f..20f,
            emptySet(),
            StatType.ATK_PERCENT
        )
        assertEquals(1, result.size)
    }

    @Test
    fun invoke_withMultipleFilters_appliesAllFilters() {
        val set = ArtifactSet(id = 1, name = "Crimson Witch", iconUrl = "")
        val result = useCase(
            testArtifacts,
            "Crimson",
            set,
            20f..20f,
            setOf(ArtifactSlot.SANDS_OF_EON),
            StatType.ATK_PERCENT
        )
        assertEquals(1, result.size)
    }

    @Test
    fun invoke_withSearchQueryMatchingArtifactName_prioritizesResult() {
        val result = useCase(
            testArtifacts,
            "Crimson Witch Flower",
            null,
            0f..20f,
            emptySet(),
            null
        )
        assertEquals(1, result.size)
        assertEquals(1, result.first().id)
    }

    @Test
    fun invoke_withNoMatches_returnsEmptyList() {
        val result = useCase(
            testArtifacts,
            "NonExistent",
            null,
            0f..20f,
            emptySet(),
            null
        )
        assertEquals(0, result.size)
    }
}
