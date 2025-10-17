package com.nokaori.genshinaibuilder.data

data class Artifact(
    val id: Int = 0, // по умолчанию для нового артефакта
    val slot: ArtifactSlot, // цветок, перо, часы, кубок, корона
    val rarity: ArtifactRarity,
    val setName: String, // название сета артефактов
    val artifactName: String,
    val level: Int, // уроень прокачки
    val mainStat: ArtifactStat,
    val subStats: List<ArtifactStat>
)

sealed interface StatValue{
    data class IntValue(val value: Int): StatValue
    data class DoubleValue(val value: Double): StatValue
}

data class ArtifactStat(
    val type: StatType, // тип хар-ки
    val value: StatValue
)

enum class ArtifactRarity(val stars: Int) {
    FOUR_STARS(4),
    FIVE_STARS(5)
}

enum class ArtifactSlot(val displayName: String){
    FLOWER_OF_LIFE("Цветок жизни"),
    PLUME_OF_DEATH("Перо смерти"),
    SANDS_OF_EON("Пески времени"),
    GOBLET_OF_EONOTHEM("Кубок пространства"),
    CIRCLET_OF_LOGOS("Корона разума")
}

enum class StatType(val displayName: String, val isPercentage: Boolean){
    // Плоские
    HP("HP", false),
    ATK("Сила атаки", false),
    DEF("Защита", false),

    // Проценты
    HP_PERCENT("HP %", true),
    ATK_PERCENT("Сила атаки %", true),
    DEF_PERCENT("Защита %", true),

    // Крит
    CRIT_RATE("Шанс крит. попадания %", true),
    CRIT_DMG("Крит. урон %", true),

    // ВЭ, МС
    ENERGY_RECHARGE("Восст. энергии %", true),
    ELEMENTAL_MASTERY("Мастерство стихий", false),

    // Урон
    PYRO_DAMAGE_BONUS("Бонус Пиро урона %", true),
    HYDRO_DAMAGE_BONUS("Бонус Гидро урона %", true),
    DENDRO_DAMAGE_BONUS("Бонус Дендро урона %", true),
    ELECTRO_DAMAGE_BONUS("Бонус Электро урона %", true),
    ANEMO_DAMAGE_BONUS("Бонус Анемо урона %", true),
    CRYO_DAMAGE_BONUS("Бонус Крио урона %", true),
    GEO_DAMAGE_BONUS("Бонус Гео урона %", true),
    PHYSICAL_DAMAGE_BONUS("Бонус физ. урона %", true),

    // Лечение
    HEALING_BONUS("Бонус лечения %", true)
}