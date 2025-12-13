package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponPromotionEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponRefinementEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponDetailDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponItemDto
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import com.nokaori.genshinaibuilder.data.remote.mapper.parseYattaStatType
import com.nokaori.genshinaibuilder.data.remote.mapper.parseYattaWeaponType

private const val ASSETS_URL = "https://gi.yatta.moe/assets/UI"

fun YattaWeaponItemDto.toEntity(): WeaponEntity {
    val safeId = this.id
    val safeName = this.name ?: "Unknown Weapon"
    val safeRank = this.rank ?: 1
    val safeType = this.type ?: "UNKNOWN"
    val safeSpecialProp = this.specialProp ?: "NONE"
    val safeIcon = this.icon ?: "UI_EquipIcon_Sword_Blunt"

    return WeaponEntity(
        id = safeId.toIntOrNull() ?: safeId.hashCode(),
        name = safeName,
        type = parseYattaWeaponType(safeType),
        rarity = safeRank,
        baseAtkLvl1 = 0f,
        subStatType = parseYattaStatType(safeSpecialProp),
        subStatBaseValue = null,
        atkCurveId = "", 
        subStatCurveId = null,
        iconUrl = "$ASSETS_URL/$safeIcon.png"
    )
}

fun WeaponEntity.updateWithDetails(dto: YattaWeaponDetailDto): WeaponEntity {
    val upgrade = dto.upgrade ?: return this 

    val baseAtkProp = upgrade.props?.find { it.propType == "FIGHT_PROP_BASE_ATTACK" }
    val subStatProp = upgrade.props?.find { it.propType == dto.specialProp }

    val parsedSubStatType = parseYattaStatType(dto.specialProp ?: "NONE")
    val finalSubStatType = if (parsedSubStatType == StatType.UNKNOWN) null else parsedSubStatType

    return this.copy(
        baseAtkLvl1 = baseAtkProp?.initValue?.toFloat() ?: 0f,
        atkCurveId = baseAtkProp?.curveId ?: "",
        subStatType = finalSubStatType,
        subStatBaseValue = subStatProp?.initValue?.toFloat(), 
        subStatCurveId = subStatProp?.curveId 
    )
}

fun mapWeaponRefinements(weaponId: Int, dto: YattaWeaponDetailDto): WeaponRefinementEntity? {
    val affixMap = dto.affix ?: return null
    val firstAffix = affixMap.values.firstOrNull() ?: return null
    
    val passiveName = firstAffix.name ?: "Unknown Passive"

    val descriptions = (0..4).map { index ->
        firstAffix.upgrade?.get(index.toString()) ?: ""
    }

    return WeaponRefinementEntity(
        weaponId = weaponId,
        passiveName = passiveName,
        descriptions = descriptions.filter { it.isNotBlank() }.map { cleanDescription(it) }
    )
}

fun mapWeaponPromotions(weaponId: Int, dto: YattaWeaponDetailDto): List<WeaponPromotionEntity> {
    val promoteList = dto.upgrade?.promote ?: return emptyList()

    return promoteList.map { pDto ->
        val addProps = pDto.addProps ?: emptyMap()

        WeaponPromotionEntity(
            weaponId = weaponId,
            ascensionLevel = pDto.level ?: 0,
            addAtk = addProps["FIGHT_PROP_BASE_ATTACK"]?.toFloat() ?: 0f,
            addSubStat = addProps[dto.specialProp]?.toFloat() 
        )
    }
}

private fun cleanDescription(raw: String): String {
    return raw.replace(Regex("<[^>]*>"), "")
              .replace("\\n", "\n")
}