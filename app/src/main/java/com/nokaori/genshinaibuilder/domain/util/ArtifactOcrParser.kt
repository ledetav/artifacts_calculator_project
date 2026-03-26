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
        var remainingLines = lines.toMutableList()
        
        var foundSlot: ArtifactSlot? = null
        var foundLevel: Int? = null
        var mainStatType: StatType? = null
        var mainStatValue: Float? = null
        var setName: String? = null
        var setId: Int? = null
        val subStats = mutableListOf<Pair<StatType, Float>>()

        if (availablePieces.isNotEmpty() && remainingLines.isNotEmpty()) {
            val pieceMatch = remainingLines.take(4).mapIndexedNotNull { index, line ->
                val lineLower = line.lowercase()
                val match = availablePieces.firstOrNull { lineLower.contains(it.name.lowercase()) }
                if (match != null) {
                    index to Pair(match, 0)
                } else {
                    FuzzySearchUtils.findBestMatch(line, availablePieces, { it.name }, 2)?.let { index to it }
                }
            }.minByOrNull { it.second.second }

            if (pieceMatch != null) {
                foundSlot = pieceMatch.second.first.slot
                setId = pieceMatch.second.first.setId
                setName = pieceMatch.second.first.setName
                remainingLines[pieceMatch.first] = remainingLines[pieceMatch.first].replace(pieceMatch.second.first.name, "", ignoreCase = true)
            }

            val setMatch = remainingLines.mapIndexedNotNull { index, line ->
                if (line.isNotBlank()) {
                    val lineLower = line.lowercase()
                    val match = availablePieces.firstOrNull { lineLower.contains(it.setName.lowercase()) }
                    if (match != null) {
                        index to Pair(match, 0)
                    } else {
                        FuzzySearchUtils.findBestMatch(line, availablePieces, { it.setName }, 2)?.let { index to it }
                    }
                } else null
            }.minByOrNull { it.second.second }

            if (setMatch != null) {
                if (setId == null) {
                    setId = setMatch.second.first.setId
                    setName = setMatch.second.first.setName
                }
                val setLineIndex = setMatch.first
                remainingLines[setLineIndex] = remainingLines[setLineIndex].replace(setMatch.second.first.setName, "", ignoreCase = true)
                
                if (setLineIndex + 1 < remainingLines.size) {
                    remainingLines = remainingLines.subList(0, setLineIndex + 1)
                }
            } else {
                val descIndex = remainingLines.indexOfFirst { 
                    val l = it.lowercase().replace(Regex("""\s+"""), "")
                    l.contains("2предмета") || l.contains("2-piece") || l.contains("увеличивает") || l.contains("increases") || l.contains("набор") || l.contains("set")
                }
                if (descIndex != -1) {
                    remainingLines = remainingLines.subList(0, descIndex)
                }
            }
        }

        val slotMatch = remainingLines.mapIndexedNotNull { index, line ->
            val cleanLine = line.lowercase().replace(Regex("""[\s.,:]"""), "")
            var bestSlot: ArtifactSlot? = null
            var bestDist = Int.MAX_VALUE
            for (entry in slotMap.entries) {
                val key = entry.key
                if (cleanLine.contains(key)) {
                    bestSlot = entry.value
                    bestDist = 0
                    break
                }
                if (key.length > 5 && cleanLine.isNotEmpty()) {
                    val dist = FuzzySearchUtils.levenshteinDistance(cleanLine, key)
                    if (dist <= 1 && dist < bestDist) {
                        bestDist = dist
                        bestSlot = entry.value
                    }
                }
            }
            if (bestSlot != null) index to bestSlot else null
        }.firstOrNull()

        if (slotMatch != null) {
            if (foundSlot == null) foundSlot = slotMatch.second
            remainingLines[slotMatch.first] = ""
        }

        val valueRegex = Regex("""[+:]?\s*(\d+[,.]?\d*)\s*(%?)""")

        for (i in remainingLines.indices) {
            val originalLine = remainingLines[i].lowercase().replace(Regex("""\s+"""), "")
            if (originalLine.isEmpty()) continue

            var matchedStatEntry: Map.Entry<String, StatType>? = null
            var bestDist = Int.MAX_VALUE
            val cleanTextForMatch = originalLine.replace(Regex("""[\d.,+%:]"""), "")

            for (entry in statMap.entries) {
                val key = entry.key
                if (originalLine.contains(key)) {
                    matchedStatEntry = entry
                    bestDist = 0
                    break
                }
                if (key.length > 3 && cleanTextForMatch.isNotEmpty()) {
                    val dist = FuzzySearchUtils.levenshteinDistance(cleanTextForMatch, key)
                    val maxDist = if (key.length <= 6) 1 else 2
                    if (dist <= maxDist && dist < bestDist) {
                        bestDist = dist
                        matchedStatEntry = entry
                        if (dist == 0) break
                    }
                }
            }

            if (matchedStatEntry != null) {
                val statType = matchedStatEntry.value
                val isPercentageStat = originalLine.contains("%") || statType.isPercentage

                val matchResult = valueRegex.find(originalLine)
                var valueStr = matchResult?.groupValues?.get(1)?.replace(",", ".")
                
                if (valueStr != null) {
                    remainingLines[i] = originalLine.replaceFirst(matchResult!!.value, "")
                } else if (i + 1 < remainingLines.size) {
                    val nextLine = remainingLines[i + 1].lowercase().replace(Regex("""\s+"""), "")
                    val nextLineMatch = valueRegex.find(nextLine)
                    if (nextLineMatch != null) {
                        valueStr = nextLineMatch.groupValues[1].replace(",", ".")
                        remainingLines[i + 1] = nextLine.replaceFirst(nextLineMatch.value, "")
                        remainingLines[i] = ""
                    }
                } else {
                    remainingLines[i] = ""
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
        
        for (line in remainingLines) {
            val cleanLine = line.replace(Regex("""\s+"""), "")
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