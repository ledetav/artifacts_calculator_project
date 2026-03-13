package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDto
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import java.util.Locale

private const val ASSETS_URL = "https://gi.yatta.moe/assets/UI"

fun YattaAvatarDto.toEntity(language: String): CharacterEntity {
    val safeIcon = this.iconName ?: "UI_AvatarIcon_Ayaka"
    val safeElement = this.element ?: ""
    val safeWeapon = this.weaponType ?: ""
    val safeName = this.name ?: "Unknown"
    val safeId = this.id ?: "0"
    val safeRank = this.rank ?: 1

    val elementEnum = parseYattaElement(safeElement)
    val finalId = parseId(safeId, elementEnum)

    // --- ЛОГИКА ИМЕНИ ---
    val displayName = if (safeName == "Traveler") {
        val lowerCaseElement = elementEnum.name.lowercase(Locale.ROOT)
        val capitalizedElement = lowerCaseElement.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
        "$capitalizedElement Traveler"
    } else {
        safeName
    }
    val isTraveler = safeIcon.contains("PlayerBoy") || safeIcon.contains("PlayerGirl")
    val isDummy = safeId == "10000117"

    val splashUrl = if (isTraveler || isDummy) {
        "$ASSETS_URL/$safeIcon.png"
    } else {
        val splashName = safeIcon.replace("AvatarIcon", "Gacha_AvatarImg")
        "$ASSETS_URL/$splashName.png"
    }

    return CharacterEntity(
        id = finalId,
        language = language,
        name = displayName,
        rarity = safeRank,
        element = elementEnum,
        weaponType = parseYattaWeaponType(safeWeapon),
        baseHpLvl1 = 0f,
        baseAtkLvl1 = 0f,
        baseDefLvl1 = 0f,
        ascensionStatType = StatType.ATK_PERCENT,
        curveId = "GROWTH_INFO_NOT_LOADED",
        iconUrl = "$ASSETS_URL/$safeIcon.png",
        splashUrl = splashUrl
    )
}

private fun parseId(rawId: String, element: Element): Int {
    val simpleId = rawId.toIntOrNull()
    if (simpleId != null) return simpleId

    val baseIdString = rawId.split("-")[0]
    val baseId = baseIdString.toIntOrNull() ?: 0

    return if (baseId > 0) {
        baseId * 100 + element.ordinal
    } else {
        rawId.hashCode()
    }
}