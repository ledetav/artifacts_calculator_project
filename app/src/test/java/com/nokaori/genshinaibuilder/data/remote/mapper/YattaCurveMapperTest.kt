package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarCurveLevelDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponCurveLevelDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaRelicCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaRelicCurveData
import org.junit.Test
import org.junit.Assert.*

class YattaCurveMapperTest {

    @Test
    fun testYattaAvatarCurveResponseToEntities() {
        val response = YattaAvatarCurveResponse(
            code = 200,
            data = mapOf(
                "1" to YattaAvatarCurveLevelDto(
                    curveInfos = mapOf(
                        "GROW_CURVE_ATTACK_S4" to 23.0,
                        "GROW_CURVE_HP_S4" to 342.0
                    )
                ),
                "2" to YattaAvatarCurveLevelDto(
                    curveInfos = mapOf(
                        "GROW_CURVE_ATTACK_S4" to 24.5,
                        "GROW_CURVE_HP_S4" to 360.0
                    )
                )
            )
        )

        val result = response.toEntities()

        assertEquals(2, result.size)
        assertTrue(result.any { it.id == "GROW_CURVE_ATTACK_S4" })
        assertTrue(result.any { it.id == "GROW_CURVE_HP_S4" })
    }

    @Test
    fun testYattaAvatarCurveResponseToEntitiesWithInvalidLevel() {
        val response = YattaAvatarCurveResponse(
            code = 200,
            data = mapOf(
                "1" to YattaAvatarCurveLevelDto(
                    curveInfos = mapOf("GROW_CURVE_ATTACK_S4" to 23.0)
                ),
                "invalid" to YattaAvatarCurveLevelDto(
                    curveInfos = mapOf("GROW_CURVE_ATTACK_S4" to 25.0)
                )
            )
        )

        val result = response.toEntities()

        assertEquals(1, result.size)
    }

    @Test
    fun testYattaAvatarCurveResponseToEntitiesPreservesValues() {
        val response = YattaAvatarCurveResponse(
            code = 200,
            data = mapOf(
                "1" to YattaAvatarCurveLevelDto(
                    curveInfos = mapOf("GROW_CURVE_ATTACK_S4" to 23.5)
                )
            )
        )

        val result = response.toEntities()

        val attackCurve = result.find { it.id == "GROW_CURVE_ATTACK_S4" }
        assertNotNull(attackCurve)
        assertTrue(attackCurve != null)
    }

    @Test
    fun testYattaWeaponCurveResponseToEntities() {
        val response = YattaWeaponCurveResponse(
            code = 200,
            data = mapOf(
                "1" to YattaWeaponCurveLevelDto(
                    curveInfos = mapOf(
                        "GROW_CURVE_ATTACK_S4" to 48.0,
                        "GROW_CURVE_CRITICAL_S4" to 9.6
                    )
                ),
                "90" to YattaWeaponCurveLevelDto(
                    curveInfos = mapOf(
                        "GROW_CURVE_ATTACK_S4" to 608.0,
                        "GROW_CURVE_CRITICAL_S4" to 122.0
                    )
                )
            )
        )

        val result = response.toEntities()

        assertEquals(2, result.size)
        assertTrue(result.any { it.id == "GROW_CURVE_ATTACK_S4" })
        assertTrue(result.any { it.id == "GROW_CURVE_CRITICAL_S4" })
    }

    @Test
    fun testYattaWeaponCurveResponseToEntitiesWithInvalidLevel() {
        val response = YattaWeaponCurveResponse(
            code = 200,
            data = mapOf(
                "1" to YattaWeaponCurveLevelDto(
                    curveInfos = mapOf("GROW_CURVE_ATTACK_S4" to 48.0)
                ),
                "abc" to YattaWeaponCurveLevelDto(
                    curveInfos = mapOf("GROW_CURVE_ATTACK_S4" to 50.0)
                )
            )
        )

        val result = response.toEntities()

        assertEquals(1, result.size)
    }

    @Test
    fun testYattaWeaponCurveResponseToEntitiesPreservesValues() {
        val response = YattaWeaponCurveResponse(
            code = 200,
            data = mapOf(
                "1" to YattaWeaponCurveLevelDto(
                    curveInfos = mapOf("GROW_CURVE_ATTACK_S4" to 48.5)
                )
            )
        )

        val result = response.toEntities()

        val attackCurve = result.find { it.id == "GROW_CURVE_ATTACK_S4" }
        assertNotNull(attackCurve)
        assertTrue(attackCurve != null)
    }

    @Test
    fun testYattaRelicCurveResponseToEntitiesWithRankedStats() {
        val response = YattaRelicCurveResponse(
            code = 200,
            data = YattaRelicCurveData(
                initial = emptyMap(),
                ranked = mapOf(
                    "5" to mapOf(
                        "1" to mapOf(
                            "FIGHT_PROP_HP" to 717.0,
                            "FIGHT_PROP_ATTACK" to 47.0
                        ),
                        "20" to mapOf(
                            "FIGHT_PROP_HP" to 1432.0,
                            "FIGHT_PROP_ATTACK" to 94.0
                        )
                    )
                ),
                affix = emptyMap()
            )
        )

        val result = response.toEntities()

        assertTrue(result.any { it.id == "ARTIFACT_RANK_5_MAIN_FIGHT_PROP_HP" })
        assertTrue(result.any { it.id == "ARTIFACT_RANK_5_MAIN_FIGHT_PROP_ATTACK" })
    }

    @Test
    fun testYattaRelicCurveResponseToEntitiesWithAffixStats() {
        val response = YattaRelicCurveResponse(
            code = 200,
            data = YattaRelicCurveData(
                initial = emptyMap(),
                ranked = emptyMap(),
                affix = mapOf(
                    "5" to mapOf(
                        "FIGHT_PROP_HP_PERCENT" to listOf(4.08, 4.59, 5.1, 5.61, 6.12),
                        "FIGHT_PROP_ATTACK_PERCENT" to listOf(2.7, 3.04, 3.38, 3.72, 4.06)
                    )
                )
            )
        )

        val result = response.toEntities()

        assertTrue(result.any { it.id == "ARTIFACT_RANK_5_SUB_FIGHT_PROP_HP_PERCENT" })
        assertTrue(result.any { it.id == "ARTIFACT_RANK_5_SUB_FIGHT_PROP_ATTACK_PERCENT" })
    }

    @Test
    fun testYattaRelicCurveResponseToEntitiesWithInvalidLevel() {
        val response = YattaRelicCurveResponse(
            code = 200,
            data = YattaRelicCurveData(
                initial = emptyMap(),
                ranked = mapOf(
                    "5" to mapOf(
                        "invalid" to mapOf("FIGHT_PROP_HP" to 717.0),
                        "1" to mapOf("FIGHT_PROP_HP" to 717.0)
                    )
                ),
                affix = emptyMap()
            )
        )

        val result = response.toEntities()

        assertTrue(result.any { it.id == "ARTIFACT_RANK_5_MAIN_FIGHT_PROP_HP" })
    }

    @Test
    fun testYattaRelicCurveResponseToEntitiesPreservesRankedValues() {
        val response = YattaRelicCurveResponse(
            code = 200,
            data = YattaRelicCurveData(
                initial = emptyMap(),
                ranked = mapOf(
                    "5" to mapOf(
                        "1" to mapOf("FIGHT_PROP_HP" to 717.5)
                    )
                ),
                affix = emptyMap()
            )
        )

        val result = response.toEntities()

        val hpCurve = result.find { it.id == "ARTIFACT_RANK_5_MAIN_FIGHT_PROP_HP" }
        assertNotNull(hpCurve)
        assertTrue(hpCurve != null)
    }

    @Test
    fun testYattaRelicCurveResponseToEntitiesPreservesAffixValues() {
        val response = YattaRelicCurveResponse(
            code = 200,
            data = YattaRelicCurveData(
                initial = emptyMap(),
                ranked = emptyMap(),
                affix = mapOf(
                    "5" to mapOf(
                        "FIGHT_PROP_HP_PERCENT" to listOf(4.08, 4.59, 5.1)
                    )
                )
            )
        )

        val result = response.toEntities()

        val hpPercentCurve = result.find { it.id == "ARTIFACT_RANK_5_SUB_FIGHT_PROP_HP_PERCENT" }
        assertNotNull(hpPercentCurve)
        assertEquals(3, hpPercentCurve?.points?.size)
    }

    @Test
    fun testYattaAvatarCurveResponseToEntitiesWithEmptyData() {
        val response = YattaAvatarCurveResponse(
            code = 200,
            data = emptyMap()
        )

        val result = response.toEntities()

        assertEquals(0, result.size)
    }

    @Test
    fun testYattaWeaponCurveResponseToEntitiesWithEmptyData() {
        val response = YattaWeaponCurveResponse(
            code = 200,
            data = emptyMap()
        )

        val result = response.toEntities()

        assertEquals(0, result.size)
    }

    @Test
    fun testYattaRelicCurveResponseToEntitiesWithEmptyData() {
        val response = YattaRelicCurveResponse(
            code = 200,
            data = YattaRelicCurveData(
                initial = emptyMap(),
                ranked = emptyMap(),
                affix = emptyMap()
            )
        )

        val result = response.toEntities()

        assertEquals(0, result.size)
    }
}
