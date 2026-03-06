package com.nokaori.genshinaibuilder.domain.util

data class ParsedArtifactData(
    val level: Int? = null,
    val mainStatName: String? = null,
    val mainStatValue: String? = null,
    val subStats: List<Pair<String, String>> = emptyList(),
    val rawText: String = ""
)

object ArtifactOcrParser {
    fun parse(rawText: String): ParsedArtifactData {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        
        var level: Int? = null
        var mainStatName: String? = null
        var mainStatValue: String? = null
        val subStats = mutableListOf<Pair<String, String>>()
        
        // Паттерн для уровня: ровно "+число", например "+20"
        val levelRegex = Regex("""^\+(\d{1,2})$""")
        // Паттерн для подстатов: "Текст + Число(с % или без)" 
        // Например: "КРИТ. урон+14.0%" или "Мастерство стихий+42"
        val subStatRegex = Regex("""^([А-Яа-яA-Za-z\s.-]+)\+([\d.]+%?)$""")
        
        for (i in lines.indices) {
            val line = lines[i]
            
            // Ищем уровень и главный стат (обычно идет сразу после уровня)
            if (level == null && levelRegex.matches(line)) {
                level = levelRegex.find(line)?.groupValues?.get(1)?.toIntOrNull()
                
                if (i + 1 < lines.size && !lines[i + 1].contains("+")) {
                    mainStatName = lines[i + 1]
                }
                if (i + 2 < lines.size) {
                    mainStatValue = lines[i + 2]
                }
                continue
            }
            
            // Ищем подстаты
            val subMatch = subStatRegex.find(line)
            if (subMatch != null) {
                val name = subMatch.groupValues[1].trim()
                val value = subMatch.groupValues[2].trim()
                // Отсеиваем случайные совпадения (названия статов не бывают слишком длинными)
                if (name.length in 2..30) {
                    subStats.add(name to value)
                }
            }
        }
        
        return ParsedArtifactData(
            level = level,
            mainStatName = mainStatName,
            mainStatValue = mainStatValue,
            subStats = subStats,
            rawText = rawText
        )
    }
}