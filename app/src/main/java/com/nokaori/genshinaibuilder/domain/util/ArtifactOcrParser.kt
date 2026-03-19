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
        val linesLowercase = lines.map { it.lowercase() }
        
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
                    maxAllowedDistance = 3 // Увеличено для лучшего срабатывания
                )
                if (match != null && match.second < globalMinDistance) {
                    globalMinDistance = match.second
                    globalBestMatch = match.first
                }
            }

            if (globalBestMatch != null) {
                foundSlot = globalBestMatch.slot
                setId = globalBestMatch.setId
                // Если в PieceMatchInfo есть поле setName, можно его привязать: setName = globalBestMatch.setName
            }
        }

        val valueRegex = Regex("""[+:]?\s*(\d+[,.]?\d*)\s*(%?)""")
        // Паттерн для поиска уровня вида "+20", "+ 15"
        val levelRegex = Regex("""\+\s*([0-2]?[0-9])\b""") 

        for (i in linesLowercase.indices) {
            val originalLineLower = linesLowercase[i]
            val cleanLine = originalLineLower.replace(Regex("""\s+"""), "")

            // Поиск слота фоллбэком (если база PieceMatchInfo не помогла)
            if (foundSlot == null) {
                foundSlot = slotMap.entries.firstOrNull { cleanLine.contains(it.key) }?.value
            }

            // Поиск уровня (допускаем пробелы, поэтому ищем в originalLineLower)
            if (foundLevel == null) {
                val levelMatch = levelRegex.find(originalLineLower)
                if (levelMatch != null) {
                    val lvl = levelMatch.groupValues[1].toIntOrNull()
                    if (lvl != null && lvl <= 20) {
                        foundLevel = lvl
                        // Если строка очень короткая (только уровень), пропускаем поиск статов в ней
                        if (cleanLine.length <= 5) continue
                    }
                }
            }

            val matchedStatEntry = statMap.entries.firstOrNull { cleanLine.contains(it.key) }
            if (matchedStatEntry != null) {
                val statType = matchedStatEntry.value
                val isPercentageStat = cleanLine.contains("%") || statType.isPercentage

                // Вырезаем название стата, чтобы не спутать цифры из названия со значением
                val cleanLineWithoutStatName = cleanLine.replace(matchedStatEntry.key, "")
                val matchResult = valueRegex.find(cleanLineWithoutStatName)
                
                var valueStr = matchResult?.groupValues?.get(1)?.replace(",", ".")
                
                // Если значение не нашли на текущей строке, ищем на следующей
                if (valueStr == null && i + 1 < linesLowercase.size) {
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
                        // Добавляем подстат, только если такого еще нет (защита от дублирования строк OCR)
                        if (subStats.none { it.first == finalStatType && it.second == valueFloat }) {
                            subStats.add(finalStatType to valueFloat)
                        }
                    }
                }
            }
        }
        
        // Попытка выцепить имя сета из текста, если оно не нашлось по базе (Берем самую длинную строку без цифр)
        if (setId == null && setName == null && lines.isNotEmpty()) {
            setName = lines.firstOrNull { !it.contains(Regex("""\d""")) && it.length > 6 && !it.startsWith("+") }
        }

        return ParsedArtifactData(
            slot = foundSlot,
            level = foundLevel ?: 0, // Fallback, чтобы парсер не возвращал null-уровень и не падал
            mainStatType = mainStatType,
            mainStatValue = mainStatValue,
            subStats = subStats,
            setName = setName,
            setId = setId,
            rawText = rawText
        )
    }
}