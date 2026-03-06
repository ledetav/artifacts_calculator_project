package com.nokaori.genshinaibuilder.domain.util

import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType

data class ParsedArtifactData(
    val slot: ArtifactSlot? = null,
    val level: Int? = null,
    val mainStatType: StatType? = null,
    val mainStatValue: Float? = null,
    val subStats: List<Pair<StatType, Float>> = emptyList(),
    val rawText: String = ""
)

object ArtifactOcrParser {

    // Карты сопоставления. Ключи специально написаны БЕЗ ПРОБЕЛОВ и в нижнем регистре
    private val slotMap = mapOf(
        "цветокжизни" to ArtifactSlot.FLOWER_OF_LIFE,
        "перосмерти" to ArtifactSlot.PLUME_OF_DEATH,
        "пескивремени" to ArtifactSlot.SANDS_OF_EON,
        "кубокпространства" to ArtifactSlot.GOBLET_OF_EONOTHEM,
        "коронаразума" to ArtifactSlot.CIRCLET_OF_LOGOS
    )

    private val statMap = mapOf(
        "нр" to StatType.HP, // Обычная H (аш)
        "hp" to StatType.HP, // Английская
        "хп" to StatType.HP, // Русская (иногда так читается)
        "силаатаки" to StatType.ATK,
        "атк" to StatType.ATK,
        "защита" to StatType.DEF,
        "мастерствостихий" to StatType.ELEMENTAL_MASTERY,
        "восст.энергии" to StatType.ENERGY_RECHARGE,
        "восстановлениеэнергии" to StatType.ENERGY_RECHARGE,
        "шанскрит.попадания" to StatType.CRIT_RATE,
        "шанскрита" to StatType.CRIT_RATE,
        "крит.урон" to StatType.CRIT_DMG,
        "критурон" to StatType.CRIT_DMG,
        "бонуслечения" to StatType.HEALING_BONUS,
        "бонуспироурона" to StatType.PYRO_DAMAGE_BONUS,
        "бонусгидроурона" to StatType.HYDRO_DAMAGE_BONUS,
        "бонусдендроурона" to StatType.DENDRO_DAMAGE_BONUS,
        "бонусэлектроурона" to StatType.ELECTRO_DAMAGE_BONUS,
        "бонусанемоурона" to StatType.ANEMO_DAMAGE_BONUS,
        "бонускриоурона" to StatType.CRYO_DAMAGE_BONUS,
        "бонусгеоурона" to StatType.GEO_DAMAGE_BONUS,
        "бонусфиз.урона" to StatType.PHYSICAL_DAMAGE_BONUS,
        "бонусфизическогоурона" to StatType.PHYSICAL_DAMAGE_BONUS
    )

    fun parse(rawText: String): ParsedArtifactData {
        val lines = rawText.lines().map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        
        var foundSlot: ArtifactSlot? = null
        var foundLevel: Int? = null
        var mainStatType: StatType? = null
        var mainStatValue: Float? = null
        val subStats = mutableListOf<Pair<StatType, Float>>()

        val valueRegex = Regex("""[+:]?\s*(\d+[,.]?\d*)\s*(%?)""")

        for (i in lines.indices) {
            val originalLine = lines[i]
            val cleanLine = originalLine.replace(Regex("""\s+"""), "")

            if (foundSlot == null) {
                foundSlot = slotMap.entries.firstOrNull { cleanLine.contains(it.key) }?.value
            }

            if (foundLevel == null && cleanLine.startsWith("+") && cleanLine.length <= 4) {
                foundLevel = cleanLine.replace("+", "").toIntOrNull()
                continue
            }

            val matchedStatEntry = statMap.entries.firstOrNull { cleanLine.contains(it.key) }
            if (matchedStatEntry != null) {
                val statType = matchedStatEntry.value
                val isPercentageStat = cleanLine.contains("%") || statType.isPercentage

                val matchResult = valueRegex.find(cleanLine.replace(matchedStatEntry.key, ""))
                
                var valueStr = matchResult?.groupValues?.get(1)?.replace(",", ".")
                
                if (valueStr == null && i + 1 < lines.indices.last) {
                    val nextLineMatch = valueRegex.find(lines[i + 1])
                    if (nextLineMatch != null) {
                        valueStr = nextLineMatch.groupValues[1].replace(",", ".")
                    }
                }

                val valueFloat = valueStr?.toFloatOrNull()

                if (valueFloat != null) {
                    val finalStatType = if (isPercentageStat && !statType.isPercentage) {
                        when (statType) {
                            StatType.HP -> StatType.HP_PERCENT
                            StatType.ATK -> StatType.ATK_PERCENT
                            StatType.DEF -> StatType.DEF_PERCENT
                            else -> statType
                        }
                    } else statType
                    if (mainStatType == null) {
                        mainStatType = finalStatType
                        mainStatValue = valueFloat
                    } else {
                        subStats.add(finalStatType to valueFloat)
                    }
                }
            }
        }

        return ParsedArtifactData(
            slot = foundSlot,
            level = foundLevel,
            mainStatType = mainStatType,
            mainStatValue = mainStatValue,
            subStats = subStats,
            rawText = rawText
        )
    }
}