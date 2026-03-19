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
        "мс" to StatType.ELEMENTAL_MASTERY,
        "восст.энергии" to StatType.ENERGY_RECHARGE,
        "восстановлениеэнергии" to StatType.ENERGY_RECHARGE,
        "вэ" to StatType.ENERGY_RECHARGE,
        "шанскрит.попадания" to StatType.CRIT_RATE,
        "шанскрита" to StatType.CRIT_RATE,
        "кш" to StatType.CRIT_RATE,
        "крит.урон" to StatType.CRIT_DMG,
        "критурон" to StatType.CRIT_DMG,
        "ку" to StatType.CRIT_DMG,
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
        
        val cleanText = rawText.lowercase().replace(Regex("""\s+"""), "")
        
        var foundSlot: ArtifactSlot? = null
        var foundLevel: Int? = null
        var mainStatType: StatType? = null
        var mainStatValue: Float? = null
        var setName: String? = null
        var setId: Int? = null
        val subStats = mutableListOf<Pair<StatType, Float>>()

        if (availablePieces.isNotEmpty() && lines.isNotEmpty()) {
            var globalBestMatch: PieceMatchInfo? = null
            var globalMinDistance = Int.MAX_VALUE

            for (line in lines) {
                val match = FuzzySearchUtils.findBestMatch(
                    query = line,
                    candidates = availablePieces,
                    textSelector = { it.name },
                    maxAllowedDistance = 3
                )
                if (match != null && match.second < globalMinDistance) {
                    globalMinDistance = match.second
                    globalBestMatch = match.first
                }
            }

            if (globalBestMatch != null) {
                foundSlot = globalBestMatch.slot
                setId = globalBestMatch.setId
            }
        }

        if (foundSlot == null) {
            foundSlot = slotMap.entries.firstOrNull { cleanText.contains(it.key) }?.value
        }

        val levelRegex = Regex("""\+([0-2]?[0-9])(?!\d)""")
        val levelMatch = levelRegex.find(cleanText)
        if (levelMatch != null) {
            foundLevel = levelMatch.groupValues[1].toIntOrNull()
        }

        val statKeysPattern = statMap.keys.sortedByDescending { it.length }.joinToString("|") { Regex.escape(it) }
        
        val statsRegex = Regex("""($statKeysPattern)[^0-9]*?(\d+[,.]?\d*%?)""")
        
        val statMatches = statsRegex.findAll(cleanText).toList()

        statMatches.forEachIndexed { index, match ->
            val statName = match.groupValues[1]
            val valueStrRaw = match.groupValues[2]
            
            val statType = statMap[statName] ?: return@forEachIndexed
            val isPercentageStat = valueStrRaw.contains("%") || statType.isPercentage
            
            val valueFloat = valueStrRaw.replace("%", "").replace(",", ".").toFloatOrNull() ?: return@forEachIndexed

            val finalStatType = if (isPercentageStat && !statType.isPercentage) {
                when (statType) {
                    StatType.HP -> StatType.HP_PERCENT
                    StatType.ATK -> StatType.ATK_PERCENT
                    StatType.DEF -> StatType.DEF_PERCENT
                    else -> statType
                }
            } else statType

            if (index == 0) {
                mainStatType = finalStatType
                mainStatValue = valueFloat
            } else {
                if (subStats.none { it.first == finalStatType && it.second == valueFloat }) {
                    subStats.add(finalStatType to valueFloat)
                }
            }
        }

        if (setId == null && setName == null && lines.isNotEmpty()) {
            setName = lines.firstOrNull { !it.contains(Regex("""\d""")) && it.length > 6 && !it.startsWith("+") }
        }

        return ParsedArtifactData(
            slot = foundSlot,
            level = foundLevel ?: 0,
            mainStatType = mainStatType,
            mainStatValue = mainStatValue,
            subStats = subStats,
            setName = setName,
            setId = setId,
            rawText = rawText
        )
    }
}