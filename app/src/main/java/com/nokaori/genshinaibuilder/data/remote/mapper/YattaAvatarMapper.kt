package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDto
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType

private const val ASSETS_URL = "https://gi.yatta.moe/assets/UI"

fun YattaAvatarDto.toEntity(): CharacterEntity {
    // Логика формирования ссылки на Сплэш-арт
    // iconName: "UI_AvatarIcon_Ayaka"
    // Нужен: "UI_Gacha_AvatarImg_Ayaka"
    val splashName = this.iconName.replace("AvatarIcon", "Gacha_AvatarImg")

    return CharacterEntity(
        id = this.id,
        name = this.name,
        rarity = this.rank,
        element = parseElement(this.element),
        weaponType = parseWeaponType(this.weaponType),

        // --- ЗАГЛУШКИ ДЛЯ БАЗОВЫХ СТАТОВ ---
        // Общий endpoint не возвращает цифры HP/ATK.
        // Мы заполним их 0, а позже сделаем отдельный запрос деталей (api/v2/en/avatar/{id}).
        baseHpLvl1 = 0f,
        baseAtkLvl1 = 0f,
        baseDefLvl1 = 0f,
        ascensionStatType = StatType.ATK_PERCENT, // Заглушка
        curveId = "GROWTH_INFO_NOT_LOADED",       // Заглушка

        // Формируем ссылки
        iconUrl = "$ASSETS_URL/${this.iconName}.png",
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
        else -> Element.ANEMO // Fallback, чтобы не крашить
    }
}

private fun parseWeaponType(raw: String): WeaponType {
    return when (raw) {
        "WEAPON_SWORD_ONE_HAND" -> WeaponType.SWORD
        "WEAPON_CLAYMORE" -> WeaponType.CLAYMORE
        "WEAPON_POLE" -> WeaponType.POLEARM
        "WEAPON_BOW" -> WeaponType.BOW
        "WEAPON_CATALYST" -> WeaponType.CATALYST
        else -> WeaponType.SWORD // Fallback
    }
}