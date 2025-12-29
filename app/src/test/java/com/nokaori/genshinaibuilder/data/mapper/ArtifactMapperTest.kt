package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.entity.ArtifactPieceEntity
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserArtifactEntity
import com.nokaori.genshinaibuilder.data.local.model.UserArtifactComplete
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.StatValue
import org.junit.Test
import org.junit.Assert.*

class ArtifactMapperTest {

    @Test
    fun testUserArtifactCompleteToDomain() {
        val userArtifact = UserArtifactEntity(
            id = 1,
            setId = 1,
            slot = ArtifactSlot.FLOWER_OF_LIFE,
            rarity = 5,
            level = 20,
            isLocked = true,
            mainStatType = StatType.HP,
            mainStatValue = 4780f,
            subStats = listOf(
                Stat(StatType.ATK_PERCENT, StatValue.DoubleValue(12.4)),
                Stat(StatType.CRIT_RATE, StatValue.DoubleValue(3.1))
            ),
            equippedCharacterId = null
        )

        val setEntity = ArtifactSetEntity(
            id = 1,
            name = "Crimson Witch of Flames",
            rarities = listOf(4, 5),
            bonus2pc = "Pyro DMG Bonus +15%",
            bonus4pc = "Increases Overloaded damage by 40%",
            iconUrl = "https://example.com/set.png"
        )

        val pieceEntity = ArtifactPieceEntity(
            id = 11,
            setId = 1,
            slot = ArtifactSlot.FLOWER_OF_LIFE,
            name = "Witch's Flower of Life",
            iconUrl = "https://example.com/piece.png"
        )

        val complete = UserArtifactComplete(
            userArtifact = userArtifact,
            setEntity = setEntity,
            pieceEntity = pieceEntity
        )

        val result = complete.toDomain()

        assertEquals(1, result.id)
        assertEquals(ArtifactSlot.FLOWER_OF_LIFE, result.slot)
        assertEquals(Rarity.FIVE_STARS, result.rarity)
        assertEquals("Crimson Witch of Flames", result.setName)
        assertEquals("Witch's Flower of Life", result.artifactName)
        assertEquals(20, result.level)
        assertTrue(result.isLocked)
        assertEquals(StatType.HP, result.mainStat.type)
        assertEquals(4780, (result.mainStat.value as StatValue.IntValue).value)
        assertEquals(2, result.subStats.size)
    }

    @Test
    fun testArtifactSetEntityToDomain() {
        val setEntity = ArtifactSetEntity(
            id = 2,
            name = "Noblesse Oblige",
            rarities = listOf(4, 5),
            bonus2pc = "Elemental Burst DMG +20%",
            bonus4pc = "Elemental Burst DMG +25%, ATK +20%",
            iconUrl = "https://example.com/noblesse.png"
        )

        val pieceEntities = listOf(
            ArtifactPieceEntity(
                id = 21,
                setId = 2,
                slot = ArtifactSlot.FLOWER_OF_LIFE,
                name = "Royal Flora",
                iconUrl = "https://example.com/flower.png"
            )
        )

        val result = setEntity.toDomain(pieceEntities)

        assertEquals(2, result.id)
        assertEquals("Noblesse Oblige", result.name)
        assertEquals(2, result.rarities.size)
        assertEquals("Elemental Burst DMG +20%", result.bonus2pc)
        assertEquals("Elemental Burst DMG +25%, ATK +20%", result.bonus4pc)
        assertEquals(1, result.pieces.size)
    }

    @Test
    fun testArtifactSetEntityToDomainWithoutPieces() {
        val setEntity = ArtifactSetEntity(
            id = 3,
            name = "Viridescent Venerer",
            rarities = listOf(4, 5),
            bonus2pc = "Anemo DMG Bonus +15%",
            bonus4pc = "Increases Swirl DMG by 60%",
            iconUrl = "https://example.com/viridescent.png"
        )

        val result = setEntity.toDomain()

        assertEquals(3, result.id)
        assertEquals("Viridescent Venerer", result.name)
        assertEquals(0, result.pieces.size)
    }

    @Test
    fun testArtifactPieceEntityToDomain() {
        val pieceEntity = ArtifactPieceEntity(
            id = 31,
            setId = 3,
            slot = ArtifactSlot.CIRCLET_OF_LOGOS,
            name = "Circlet of Logos",
            iconUrl = "https://example.com/circlet.png"
        )

        val result = pieceEntity.toDomain()

        assertEquals(31, result.id)
        assertEquals("Circlet of Logos", result.name)
        assertEquals(ArtifactSlot.CIRCLET_OF_LOGOS, result.slot)
        assertEquals("https://example.com/circlet.png", result.iconUrl)
    }

    @Test
    fun testUserArtifactCompleteToDomainWithPercentageMainStat() {
        val userArtifact = UserArtifactEntity(
            id = 2,
            setId = 1,
            slot = ArtifactSlot.SANDS_OF_EON,
            rarity = 5,
            level = 20,
            isLocked = false,
            mainStatType = StatType.ATK_PERCENT,
            mainStatValue = 46.6f,
            subStats = emptyList(),
            equippedCharacterId = null
        )

        val setEntity = ArtifactSetEntity(
            id = 1,
            name = "Test Set",
            rarities = listOf(5),
            bonus2pc = "Test",
            bonus4pc = "Test",
            iconUrl = "https://example.com/test.png"
        )

        val pieceEntity = ArtifactPieceEntity(
            id = 12,
            setId = 1,
            slot = ArtifactSlot.SANDS_OF_EON,
            name = "Sands",
            iconUrl = "https://example.com/sands.png"
        )

        val complete = UserArtifactComplete(
            userArtifact = userArtifact,
            setEntity = setEntity,
            pieceEntity = pieceEntity
        )

        val result = complete.toDomain()

        assertEquals(StatType.ATK_PERCENT, result.mainStat.type)
        assertEquals(46.6, (result.mainStat.value as StatValue.DoubleValue).value, 0.01)
    }
}
