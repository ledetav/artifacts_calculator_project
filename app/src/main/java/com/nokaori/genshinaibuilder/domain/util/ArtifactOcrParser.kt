package com.nokaori.genshinaibuilder.domain.util

import android.os.Parcelable
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.repository.PieceMatchInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParsedArtifactData(
    val slot: ArtifactSlot? = null,
    val level: Int? = null,
    val mainStatType: StatType? = null,
    val mainStatValue: Float? = null,
    val subStats: List<Pair<StatType, Float>> = emptyList(),
    val setName: String? = null,
    val setId: Int? = null,
    val rawText: String = ""
) : Parcelable

object ArtifactOcrParser {

    private val slotMap = mapOf(
        "цветокжизни" to ArtifactSlot.FLOWER_OF_LIFE,
        "перосмерти" to ArtifactSlot.PLUME_OF_DEATH,
        "пескивремени" to ArtifactSlot.SANDS_OF_EON,
        "кубокпространства" to ArtifactSlot.GOBLET_OF_EONOTHEM,
        "коронаразума" to ArtifactSlot.CIRCLET_OF_LOGOS
    )

    private val statMap = mapOf(
        "нр" to StatType.HP, 
        "hp" to StatType.HP, 
        "хп" to StatType.HP, 
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

    fun parse(rawText: String, availablePieces: List<PieceMatchInfo> = emptyList()): ParsedArtifactData {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val linesLowercase = lines.map { it.lowercase() }
        
        var foundSlot: ArtifactSlot? = null
        var foundLevel: Int? = null
        var mainStatType: StatType? = null
        var mainStatValue: Float? = null
        var setName: String? = null
        var setId: Int? = null
        val subStats = mutableListOf<Pair<StatType, Float>>()

        if (availablePieces.isNotEmpty() && lines.isNotEmpty()) {
            val candidates = lines.take(2).mapNotNull { line ->
                FuzzySearchUtils.findBestMatch(
                    query = line,
                    candidates = availablePieces,
                    textSelector = { it.name },
                    maxAllowedDistance = 2
                )
            }

            val bestMatch = candidates.minByOrNull { it.second }?.first

            if (bestMatch != null) {
                foundSlot = bestMatch.slot
                setId = bestMatch.setId
            }
        }

        val valueRegex = Regex("""[+:]?\s*(\d+[,.]?\d*)\s*(%?)""")

        for (i in linesLowercase.indices) {
            val cleanLine = linesLowercase[i].replace(Regex("""\s+"""), "")

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
                
                if (valueStr == null && i + 1 < linesLowercase.indices.last) {
                    val nextLineMatch = valueRegex.find(linesLowercase[i + 1])
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
        
        if (setId == null && lines.isNotEmpty() && !linesLowercase[0].contains(Regex("[+:]")) && foundSlot == null) {
            setName = lines[0]
        }

        return ParsedArtifactData(
            slot = foundSlot,
            level = foundLevel,
            mainStatType = mainStatType,
            mainStatValue = mainStatValue,
            subStats = subStats,
            setName = setName,
            setId = setId,
            rawText = rawText
        )
    }
}