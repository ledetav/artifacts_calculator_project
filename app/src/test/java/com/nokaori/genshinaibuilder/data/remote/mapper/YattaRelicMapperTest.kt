package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.remote.dto.YattaRelicDetailDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaRelicPieceDto
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import org.junit.Test
import org.junit.Assert.*

class YattaRelicMapperTest {

    @Test
    fun testYattaRelicDetailDtoToSetEntity() {
        val dto = YattaRelicDetailDto(
            id = 15001,
            name = "Crimson Witch of Flames",
            rarities = listOf(4, 5),
            bonusMap = mapOf(
                "1" to "Pyro DMG Bonus +15%",
                "2" to "Increases Overloaded damage by 40%"
            ),
            icon = "UI_RelicIcon_15001_4",
            suit = null
        )

        val result = dto.toSetEntity()

        assertEquals(15001, result.id)
        assertEquals("Crimson Witch of Flames", result.name)
        assertEquals(listOf(4, 5), result.rarities)
        assertEquals("Pyro DMG Bonus +15%", result.bonus2pc)
        assertEquals("Increases Overloaded damage by 40%", result.bonus4pc)
        assertTrue(result.iconUrl.contains("UI_RelicIcon_15001_4"))
    }

    @Test
    fun testYattaRelicDetailDtoToSetEntityWithNullBonuses() {
        val dto = YattaRelicDetailDto(
            id = 15002,
            name = "Noblesse Oblige",
            rarities = listOf(4, 5),
            bonusMap = null,
            icon = "UI_RelicIcon_15002_4",
            suit = null
        )

        val result = dto.toSetEntity()

        assertEquals(15002, result.id)
        assertEquals("Noblesse Oblige", result.name)
        assertEquals("", result.bonus2pc)
        assertEquals("", result.bonus4pc)
    }

    @Test
    fun testYattaRelicDetailDtoToSetEntityWithEmptyBonuses() {
        val dto = YattaRelicDetailDto(
            id = 15003,
            name = "Viridescent Venerer",
            rarities = listOf(4, 5),
            bonusMap = emptyMap(),
            icon = "UI_RelicIcon_15003_4",
            suit = null
        )

        val result = dto.toSetEntity()

        assertEquals("", result.bonus2pc)
        assertEquals("", result.bonus4pc)
    }



    @Test
    fun testMapRelicPiecesWithNullSuit() {
        val dto = YattaRelicDetailDto(
            id = 15006,
            name = "Test Set",
            rarities = listOf(4, 5),
            bonusMap = mapOf("1" to "Test", "2" to "Test"),
            icon = "UI_RelicIcon_15006_4",
            suit = null
        )

        val result = mapRelicPieces(15006, dto)

        assertEquals(0, result.size)
    }

    @Test
    fun testMapRelicPiecesWithEmptySuit() {
        val dto = YattaRelicDetailDto(
            id = 15007,
            name = "Test Set",
            rarities = listOf(4, 5),
            bonusMap = mapOf("1" to "Test", "2" to "Test"),
            icon = "UI_RelicIcon_15007_4",
            suit = emptyMap()
        )

        val result = mapRelicPieces(15007, dto)

        assertEquals(0, result.size)
    }

    @Test
    fun testMapRelicPiecesPreservesSetId() {
        val setId = 15008
        val dto = YattaRelicDetailDto(
            id = setId,
            name = "Test Set",
            rarities = listOf(4, 5),
            bonusMap = mapOf("1" to "Test", "2" to "Test"),
            icon = "UI_RelicIcon_15008_4",
            suit = mapOf(
                "1" to YattaRelicPieceDto("Flower", "UI_RelicIcon_15008_1"),
                "2" to YattaRelicPieceDto("Plume", "UI_RelicIcon_15008_2")
            )
        )

        val result = mapRelicPieces(setId, dto)

        result.forEach { piece ->
            assertEquals(setId, piece.setId)
        }
    }

    @Test
    fun testMapRelicPiecesGeneratesCorrectIds() {
        val setId = 15009
        val dto = YattaRelicDetailDto(
            id = setId,
            name = "Test Set",
            rarities = listOf(4, 5),
            bonusMap = mapOf("1" to "Test", "2" to "Test"),
            icon = "UI_RelicIcon_15009_4",
            suit = mapOf(
                "1" to YattaRelicPieceDto("Flower", "UI_RelicIcon_15009_1"),
                "2" to YattaRelicPieceDto("Plume", "UI_RelicIcon_15009_2")
            )
        )

        val result = mapRelicPieces(setId, dto)

        result.forEach { piece ->
            val expectedId = setId * 10 + (piece.slot.ordinal + 1)
            assertEquals(expectedId, piece.id)
        }
    }

    @Test
    fun testYattaRelicDetailDtoToSetEntityPreservesIconUrl() {
        val dto = YattaRelicDetailDto(
            id = 15010,
            name = "Test Set",
            rarities = listOf(4, 5),
            bonusMap = mapOf("1" to "Test", "2" to "Test"),
            icon = "UI_RelicIcon_15010_4",
            suit = null
        )

        val result = dto.toSetEntity()

        assertTrue(result.iconUrl.contains("UI_RelicIcon_15010_4"))
        assertTrue(result.iconUrl.contains("https://gi.yatta.moe/assets/UI/reliquary"))
    }


}
