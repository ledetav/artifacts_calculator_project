package com.nokaori.genshinaibuilder.domain.model

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

enum class ArtifactSlot(){
    FLOWER_OF_LIFE,
    PLUME_OF_DEATH,
    SANDS_OF_EON,
    GOBLET_OF_EONOTHEM,
    CIRCLET_OF_LOGOS
}

enum class StatType(val isPercentage: Boolean){
    // Плоские
    HP(false),
    ATK(false),
    DEF(false),

    // Проценты
    HP_PERCENT(true),
    ATK_PERCENT(true),
    DEF_PERCENT(true),

    // Крит
    CRIT_RATE(true),
    CRIT_DMG(true),

    // ВЭ, МС
    ENERGY_RECHARGE(true),
    ELEMENTAL_MASTERY(false),

    // Урон
    PYRO_DAMAGE_BONUS(true),
    HYDRO_DAMAGE_BONUS(true),
    DENDRO_DAMAGE_BONUS(true),
    ELECTRO_DAMAGE_BONUS(true),
    ANEMO_DAMAGE_BONUS(true),
    CRYO_DAMAGE_BONUS(true),
    GEO_DAMAGE_BONUS(true),
    PHYSICAL_DAMAGE_BONUS(true),

    // Лечение
    HEALING_BONUS(true)
}