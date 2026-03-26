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
        "floweroflife" to ArtifactSlot.FLOWER_OF_LIFE,
        "перосмерти" to ArtifactSlot.PLUME_OF_DEATH,
        "plumeofdeath" to ArtifactSlot.PLUME_OF_DEATH,
        "пескивремени" to ArtifactSlot.SANDS_OF_EON,
        "sandsofeon" to ArtifactSlot.SANDS_OF_EON,
        "кубокпространства" to ArtifactSlot.GOBLET_OF_EONOTHEM,
        "gobletofeonothem" to ArtifactSlot.GOBLET_OF_EONOTHEM,
        "коронаразума" to ArtifactSlot.CIRCLET_OF_LOGOS,
        "circletoflogos" to ArtifactSlot.CIRCLET_OF_LOGOS
    )

    private val statMap = mapOf(
        "нр" to StatType.HP,
        "hp" to StatType.HP,
        "хп" to StatType.HP,
        "силаатаки" to StatType.ATK,
        "атк" to StatType.ATK,
        "attack" to StatType.ATK,
        "atk" to StatType.ATK,
        "защита" to StatType.DEF,
        "def" to StatType.DEF,
        "defense" to StatType.DEF,
        "мастерствостихий" to StatType.ELEMENTAL_MASTERY,
        "elementalmastery" to StatType.ELEMENTAL_MASTERY,
        "восст.энергии" to StatType.ENERGY_RECHARGE,
        "восстановлениеэнергии" to StatType.ENERGY_RECHARGE,
        "energyrecharge" to StatType.ENERGY_RECHARGE,
        "шанскрит.попадания" to StatType.CRIT_RATE,
        "шанскрита" to StatType.CRIT_RATE,
        "critrate" to StatType.CRIT_RATE,
        "крит.урон" to StatType.CRIT_DMG,
        "критурон" to StatType.CRIT_DMG,
        "critdmg" to StatType.CRIT_DMG,
        "criticaldamage" to StatType.CRIT_DMG,
        "бонуслечения" to StatType.HEALING_BONUS,
        "healingbonus" to StatType.HEALING_BONUS,
        "бонуспироурона" to StatType.PYRO_DAMAGE_BONUS,
        "pyrodmgbonus" to StatType.PYRO_DAMAGE_BONUS,
        "pyrodamagebonus" to StatType.PYRO_DAMAGE_BONUS,
        "бонусгидроурона" to StatType.HYDRO_DAMAGE_BONUS,
        "hydrodmgbonus" to StatType.HYDRO_DAMAGE_BONUS,
        "hydrodamagebonus" to StatType.HYDRO_DAMAGE_BONUS,
        "бонусдендроурона" to StatType.DENDRO_DAMAGE_BONUS,
        "dendrodmgbonus" to StatType.DENDRO_DAMAGE_BONUS,
        "dendrodamagebonus" to StatType.DENDRO_DAMAGE_BONUS,
        "бонусэлектроурона" to StatType.ELECTRO_DAMAGE_BONUS,
        "electrodmgbonus" to StatType.ELECTRO_DAMAGE_BONUS,
        "electrodamagebonus" to StatType.ELECTRO_DAMAGE_BONUS,
        "бонусанемоурона" to StatType.ANEMO_DAMAGE_BONUS,
        "anemodmgbonus" to StatType.ANEMO_DAMAGE_BONUS,
        "anemodamagebonus" to StatType.ANEMO_DAMAGE_BONUS,
        "бонускриоурона" to StatType.CRYO_DAMAGE_BONUS,
        "cryodmgbonus" to StatType.CRYO_DAMAGE_BONUS,
        "cryodamagebonus" to StatType.CRYO_DAMAGE_BONUS,
        "бонусгеоурона" to StatType.GEO_DAMAGE_BONUS,
        "geodmgbonus" to StatType.GEO_DAMAGE_BONUS,
        "geodamagebonus" to StatType.GEO_DAMAGE_BONUS,
        "бонусфиз.урона" to StatType.PHYSICAL_DAMAGE_BONUS,
        "бонусфизическогоурона" to StatType.PHYSICAL_DAMAGE_BONUS,
        "physicaldmgbonus" to StatType.PHYSICAL_DAMAGE_BONUS,
        "physicaldamagebonus" to StatType.PHYSICAL_DAMAGE_BONUS
    )

    fun parse(rawText: String, availablePieces: List<PieceMatchInfo> = emptyList()): ParsedArtifactData {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val remainingLines = lines.toMutableList()
        
        var foundSlot: ArtifactSlot? = null
        var foundLevel: Int? = null
        var mainStatType: StatType? = null
        var mainStatValue: Float? = null
        var setName: String? = null
        var setId: Int? = null
        val subStats = mutableListOf<Pair<StatType, Float>>()

        if (availablePieces.isNotEmpty() && remainingLines.isNotEmpty()) {
            val pieceMatch = remainingLines.take(3).mapIndexedNotNull { index, line ->
                FuzzySearchUtils.findBestMatch(line, availablePieces, { it.name }, 2)?.let { index to it }
            }.minByOrNull { it.second.second }

            if (pieceMatch != null) {
                foundSlot = pieceMatch.second.first.slot
                setId = pieceMatch.second.first.setId
                setName = pieceMatch.second.first.setName
                remainingLines[pieceMatch.first] = ""
            }

            val setMatch = remainingLines.take(3).filter { it.isNotBlank() }.mapIndexedNotNull { _, line ->
                val idx = remainingLines.indexOf(line)
                FuzzySearchUtils.findBestMatch(line, availablePieces, { it.setName }, 2)?.let { idx to it }
            }.minByOrNull { it.second.second }

            if (setMatch != null) {
                if (setId == null) {
                    setId = setMatch.second.first.setId
                    setName = setMatch.second.first.setName
                }
                remainingLines[setMatch.first] = ""
            }
        }

        for (i in remainingLines.indices) {
            val cleanLine = remainingLines[i].lowercase().replace(Regex("""\s+"""), "")
            val matchedSlot = slotMap.entries.firstOrNull { cleanLine.contains(it.key) }
            if (matchedSlot != null) {
                if (foundSlot == null) foundSlot = matchedSlot.value
                remainingLines[i] = cleanLine.replace(matchedSlot.key, "")
            }
        }

        val valueRegex = Regex("""[+:]?\s*(\d+[,.]?\d*)\s*(%?)""")

        for (i in remainingLines.indices) {
            var line = remainingLines[i].lowercase().replace(Regex("""\s+"""), "")
            if (line.isEmpty()) continue

            var matchedStatEntry = statMap.entries.firstOrNull { line.contains(it.key) }
            while (matchedStatEntry != null) {
                val statType = matchedStatEntry.value
                val isPercentageStat = line.contains("%") || statType.isPercentage

                line = line.replaceFirst(matchedStatEntry.key, "")
                
                val matchResult = valueRegex.find(line)
                var valueStr = matchResult?.groupValues?.get(1)?.replace(",", ".")
                
                if (valueStr != null) {
                    line = line.replaceFirst(matchResult!!.value, "")
                } else if (i + 1 < remainingLines.size) {
                    val nextLine = remainingLines[i + 1].lowercase().replace(Regex("""\s+"""), "")
                    val nextLineMatch = valueRegex.find(nextLine)
                    if (nextLineMatch != null) {
                        valueStr = nextLineMatch.groupValues[1].replace(",", ".")
                        remainingLines[i + 1] = nextLine.replaceFirst(nextLineMatch.value, "")
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
                matchedStatEntry = statMap.entries.firstOrNull { line.contains(it.key) }
            }
            remainingLines[i] = line
        }
        
        for (line in remainingLines) {
            val cleanLine = line.trim()
            if (cleanLine.startsWith("+") && cleanLine.length <= 4 && !cleanLine.contains("%")) {
                val lvl = cleanLine.replace("+", "").toIntOrNull()
                if (lvl != null && lvl in 0..20) {
                    foundLevel = lvl
                    break
                }
            } else if (cleanLine.matches(Regex("""^\+?\d{1,2}$"""))) {
               val lvl = cleanLine.replace("+", "").toIntOrNull()
               if (lvl != null && lvl in 0..20 && foundLevel == null) {
                   foundLevel = lvl
               }
            }
        }

        if (setId == null && lines.isNotEmpty() && foundSlot == null) {
            if (setName == null) setName = lines[0]
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