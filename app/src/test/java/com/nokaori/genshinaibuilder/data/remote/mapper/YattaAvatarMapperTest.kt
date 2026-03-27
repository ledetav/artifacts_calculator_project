package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDto
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Test
import org.junit.Assert.*

class YattaAvatarMapperTest {



    @Test
    fun testYattaAvatarDtoToEntityWithNullValues() {
        val dto = YattaAvatarDto(
            id = null,
            name = null,
            rank = null,
            element = null,
            weaponType = null,
            iconName = null,
            releaseDate = null
        )

        val result = dto.toEntity()

        assertEquals("Unknown", result.name)
        assertEquals(1, result.rarity)
        assertEquals(Element.UNKNOWN, result.element)
        assertEquals(WeaponType.UNKNOWN, result.weaponType)
    }



    @Test
    fun testYattaAvatarDtoToEntityWithDifferentWeaponTypes() {
        val weaponTypes = listOf(
            "WEAPON_SWORD_ONE_HAND" to WeaponType.SWORD,
            "WEAPON_CLAYMORE" to WeaponType.CLAYMORE,
            "WEAPON_POLE" to WeaponType.POLEARM,
            "WEAPON_BOW" to WeaponType.BOW,
            "WEAPON_CATALYST" to WeaponType.CATALYST
        )

        weaponTypes.forEach { (weaponStr, expectedType) ->
            val dto = YattaAvatarDto(
                id = "10000001",
                name = "Test",
                rank = 5,
                element = "Pyro",
                weaponType = weaponStr,
                iconName = "UI_AvatarIcon_Test",
                releaseDate = null
            )

            val result = dto.toEntity(language = "en")
            assertEquals(expectedType, result.weaponType)
        }
    }

    @Test
    fun testYattaAvatarDtoToEntitySplashUrlForNormalCharacter() {
        val dto = YattaAvatarDto(
            id = "10000003",
            name = "Ganyu",
            rank = 5,
            element = "Cryo",
            weaponType = "WEAPON_BOW",
            iconName = "UI_AvatarIcon_Ganyu",
            releaseDate = null
        )

        val result = dto.toEntity()

        assertTrue(result.splashUrl.contains("Gacha_AvatarImg_Ganyu"))
    }

    @Test
    fun testYattaAvatarDtoToEntitySplashUrlForTraveler() {
        val dto = YattaAvatarDto(
            id = "10000005",
            name = "Traveler",
            rank = 5,
            element = "Anemo",
            weaponType = "WEAPON_SWORD_ONE_HAND",
            iconName = "UI_AvatarIcon_PlayerBoy",
            releaseDate = null
        )

        val result = dto.toEntity()

        assertTrue(result.splashUrl.contains("UI_AvatarIcon_PlayerBoy"))
    }

    @Test
    fun testYattaAvatarDtoToEntityWithRarity4() {
        val dto = YattaAvatarDto(
            id = "10000014",
            name = "Barbara",
            rank = 4,
            element = "Hydro",
            weaponType = "WEAPON_CATALYST",
            iconName = "UI_AvatarIcon_Barbara",
            releaseDate = null
        )

        val result = dto.toEntity()

        assertEquals(4, result.rarity)
    }

    @Test
    fun testYattaAvatarDtoToEntityPreservesIconUrl() {
        val dto = YattaAvatarDto(
            id = "10000001",
            name = "Fischl",
            rank = 4,
            element = "Electro",
            weaponType = "WEAPON_BOW",
            iconName = "UI_AvatarIcon_Fischl",
            releaseDate = null
        )

        val result = dto.toEntity()

        assertTrue(result.iconUrl.contains("UI_AvatarIcon_Fischl"))
        assertTrue(result.iconUrl.contains("https://gi.yatta.moe/assets/UI"))
    }
}
