package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.CharacterConstellationEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterTalentEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterPromotionEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDetailDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaTalentDto
import com.nokaori.genshinaibuilder.domain.model.TalentAttribute
import com.nokaori.genshinaibuilder.domain.model.TalentType

private const val ASSETS_URL = "https://gi.yatta.moe/assets/UI"

fun CharacterEntity.updateWithDetails(dto: YattaAvatarDetailDto): CharacterEntity {
    val hpProp = dto.upgrade.props.find { it.propType == "FIGHT_PROP_BASE_HP" }
    val atkProp = dto.upgrade.props.find { it.propType == "FIGHT_PROP_BASE_ATTACK" }
    val defProp = dto.upgrade.props.find { it.propType == "FIGHT_PROP_BASE_DEFENSE" }
    val mainCurveId = atkProp?.curveId ?: "GROW_CURVE_ATTACK_S4"

    return this.copy(
        baseHpLvl1 = hpProp?.initValue?.toFloat() ?: 0f,
        baseAtkLvl1 = atkProp?.initValue?.toFloat() ?: 0f,
        baseDefLvl1 = defProp?.initValue?.toFloat() ?: 0f,
        ascensionStatType = parseYattaStatType(dto.specialProp),
        curveId = mainCurveId
    )
}

fun mapPromotions(charId: Int, dto: YattaAvatarDetailDto): List<CharacterPromotionEntity> {
    val promoteList = dto.upgrade.promote ?: return emptyList()
    
    val specialPropKey = dto.specialProp

    return promoteList.map { pDto ->
        val propsMap = pDto.addProps ?: emptyMap()

        CharacterPromotionEntity(
            characterId = charId,
            ascensionLevel = pDto.level,
            
            addHp = propsMap["FIGHT_PROP_BASE_HP"]?.toFloat() ?: 0f,
            addAtk = propsMap["FIGHT_PROP_BASE_ATTACK"]?.toFloat() ?: 0f,
            addDef = propsMap["FIGHT_PROP_BASE_DEFENSE"]?.toFloat() ?: 0f,
            
            ascensionStatValue = propsMap[specialPropKey]?.toFloat() ?: 0f
        )
    }
}

fun mapTalentsAndConstellations(
    charId: Int, 
    dto: YattaAvatarDetailDto
): Pair<List<CharacterTalentEntity>, List<CharacterConstellationEntity>> {

    val constellationBonusMap = mutableMapOf<Int, TalentType>()

    // --- ТАЛАНТЫ ---
    val sortedTalents = dto.talents.entries.sortedBy { it.key.toIntOrNull() ?: 99 }
    
    // ИСПОЛЬЗУЕМ mapIndexed, чтобы получить порядковый номер (index)
    val talentsList = sortedTalents.mapIndexed { index, (_, tDto) ->
        val talentType = determineTalentTypeByIcon(tDto.icon, tDto.type)

        tDto.linkedConstellations?.props?.forEach { prop ->
            if (prop.type == "level") {
                constellationBonusMap[prop.id] = talentType
            }
        }

        CharacterTalentEntity(
            characterId = charId,
            orderIndex = index, // Передаем индекс
            type = talentType,  // Передаем Enum
            name = tDto.name,
            description = cleanDescription(tDto.description),
            iconUrl = "$ASSETS_URL/${tDto.icon}.png",
            scalingAttributes = parseScaling(tDto)
        )
    }

    // --- СОЗВЕЗДИЯ ---
    val constsMap = dto.constellations ?: emptyMap() // Если null, берем пустую карту
    
    val sortedConsts = constsMap.entries.sortedBy { it.key.toIntOrNull() ?: 99 }

    val constsList = sortedConsts.mapIndexed { index, (_, cDto) ->
        val targetTalent = constellationBonusMap[index]

        CharacterConstellationEntity(
            characterId = charId,
            order = index + 1,
            name = cDto.name,
            description = cleanDescription(cDto.description),
            iconUrl = "$ASSETS_URL/${cDto.icon}.png",
            talentLevelUpTarget = targetTalent
        )
    }

    return Pair(talentsList, constsList)
}

private fun determineTalentTypeByIcon(iconName: String, typeId: Int): TalentType {
    return when {
        iconName.contains("Skill_A_") -> TalentType.NORMAL_ATTACK
        iconName.contains("Skill_S_") && !iconName.contains("UI_Talent_") -> TalentType.ELEMENTAL_SKILL
        iconName.contains("Skill_E_") -> TalentType.ELEMENTAL_BURST
        typeId == 2 -> {
            if (iconName.contains("Combine") || iconName.contains("Cook") || iconName.contains("Sprint") || iconName.contains("Map")) 
                TalentType.PASSIVE_UTILITY 
            else if (iconName.contains("_05")) TalentType.PASSIVE_1
            else if (iconName.contains("_06")) TalentType.PASSIVE_2
            else TalentType.PASSIVE_1
        }
        else -> TalentType.ELEMENTAL_SKILL
    }
}

private fun parseScaling(dto: YattaTalentDto): List<TalentAttribute> {
    val promoteMap = dto.promote ?: return emptyList()
    val level1Data = promoteMap["1"] ?: return emptyList()
    val labels = level1Data.description

    val resultAttributes = mutableListOf<TalentAttribute>()

    labels.forEachIndexed { index, rawLabel ->
        if (rawLabel.isBlank()) return@forEachIndexed
        val cleanLabel = rawLabel.substringBefore("|")
        val values = mutableListOf<Float>()
        
        for (lvl in 1..15) {
            val levelData = promoteMap[lvl.toString()]
            if (levelData != null && index < levelData.params.size) {
                // Извлекаем индекс параметра из строки вида {param1:F1P} -> индекс 0
                val paramIndexMatch = Regex("""param(\d+)""").find(rawLabel)
                val paramIndex = paramIndexMatch?.groupValues?.get(1)?.toIntOrNull()?.minus(1)

                if (paramIndex != null && paramIndex >= 0 && paramIndex < levelData.params.size) {
                    values.add(levelData.params[paramIndex].toFloat())
                } else {
                    values.add(0f)
                }
            }
        }
        if (values.isNotEmpty() && values.any { it != 0f }) {
            resultAttributes.add(TalentAttribute(cleanLabel, values))
        }
    }
    return resultAttributes
}

private fun cleanDescription(raw: String): String {
    return raw.replace(Regex("<[^>]*>"), "").replace("\\n", "\n")
}