package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDto
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType

private const val ASSETS_URL = "https://gi.yatta.moe/assets/UI"

fun YattaAvatarDto.toEntity(): CharacterEntity {
    val safeIcon = this.iconName ?: "UI_AvatarIcon_Ayaka"
    val safeElement = this.element ?: ""
    val safeWeapon = this.weaponType ?: ""
    val safeName = this.name ?: "Unknown"
    val safeId = this.id ?: "0"
    val safeRank = this.rank ?: 4

    val splashName = safeIcon.replace("AvatarIcon", "Gacha_AvatarImg")
    val elementEnum = parseElement(safeElement)

    val finalId = parseId(safeId, elementEnum)

    return CharacterEntity(
            id = finalId,
            name = safeName,
            rarity = safeRank,
            element = elementEnum,
            weaponType = parseWeaponType(safeWeapon),
            baseHpLvl1 = 0f,
            baseAtkLvl1 = 0f,
            baseDefLvl1 = 0f,
            ascensionStatType = StatType.ATK_PERCENT,
            curveId = "GROWTH_INFO_NOT_LOADED",
            iconUrl = "$ASSETS_URL/$safeIcon.png",
            splashUrl = "$ASSETS_URL/$splashName.png"
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
        else -> Element.ANEMO // Дефолтное значение для неизвестных/пустых
    }
}

private fun parseWeaponType(raw: String): WeaponType {
    return when (raw) {
        "WEAPON_SWORD_ONE_HAND" -> WeaponType.SWORD
        "WEAPON_CLAYMORE" -> WeaponType.CLAYMORE
        "WEAPON_POLE" -> WeaponType.POLEARM
        "WEAPON_BOW" -> WeaponType.BOW
        "WEAPON_CATALYST" -> WeaponType.CATALYST
        else -> WeaponType.SWORD // Дефолтное значение
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
        // Если ID пришел null или совсем кривой, используем хэш-код, чтобы хоть как-то сохранить
        rawId.hashCode()
    }
}