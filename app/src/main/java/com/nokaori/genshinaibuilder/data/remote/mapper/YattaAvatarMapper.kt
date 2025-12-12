package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDto
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import java.util.Locale

private const val ASSETS_URL = "https://gi.yatta.moe/assets/UI"

fun YattaAvatarDto.toEntity(): CharacterEntity {
    val safeIcon = this.iconName ?: "UI_AvatarIcon_Ayaka"
    val safeElement = this.element ?: ""
    val safeWeapon = this.weaponType ?: ""
    val safeName = this.name ?: "Unknown"
    val safeId = this.id ?: "0"
    val safeRank = this.rank ?: 1 // Если ранк не пришел, пусть будет 1 звезда

    val elementEnum = parseElement(safeElement)
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

    // --- ЛОГИКА СПЛЭШ-АРТА ---
    // У Путешественников (PlayerBoy/Girl) и Манекена нет картинки Gacha_AvatarImg.
    // Для них мы используем ту же картинку, что и для иконки (просто портрет).
    val isTraveler = safeIcon.contains("PlayerBoy") || safeIcon.contains("PlayerGirl")
    val isDummy = safeId == "10000117" // ID Манекена (обычно)

    val splashUrl = if (isTraveler || isDummy) {
        "$ASSETS_URL/$safeIcon.png" // Фолбэк на иконку
    } else {
        // Стандартная логика для остальных
        val splashName = safeIcon.replace("AvatarIcon", "Gacha_AvatarImg")
        "$ASSETS_URL/$splashName.png"
    }

    return CharacterEntity(
        id = finalId,
        name = displayName,
        rarity = safeRank,
        element = elementEnum,
        weaponType = parseWeaponType(safeWeapon),
        baseHpLvl1 = 0f,
        baseAtkLvl1 = 0f,
        baseDefLvl1 = 0f,
        ascensionStatType = StatType.ATK_PERCENT,
        curveId = "GROWTH_INFO_NOT_LOADED",
        iconUrl = "$ASSETS_URL/$safeIcon.png",
        splashUrl = splashUrl // <-- Используем вычисленный URL
    )
}

private fun parseElement(raw: String): Element {
    return when (raw) {
        "Fire" -> Element.PYRO
        "Water" -> Element.HYDRO
        "Wind" -> Element.ANEMO
        "Electric" -> Element.ELECTRO
        "Grass" -> Element.DENDRO
        "Ice" -> Element.CRYO
        "Rock" -> Element.GEO
        else -> Element.UNKNOWN // <-- Теперь возвращаем честный UNKNOWN
    }
}

private fun parseWeaponType(raw: String): WeaponType {
    return when (raw) {
        "WEAPON_SWORD_ONE_HAND" -> WeaponType.SWORD
        "WEAPON_CLAYMORE" -> WeaponType.CLAYMORE
        "WEAPON_POLE" -> WeaponType.POLEARM
        "WEAPON_BOW" -> WeaponType.BOW
        "WEAPON_CATALYST" -> WeaponType.CATALYST
        else -> WeaponType.UNKNOWN
    }
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