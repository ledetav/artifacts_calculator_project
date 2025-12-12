package com.nokaori.genshinaibuilder.domain.model

data class Stat(
    val type: StatType,
    val value: StatValue
)

sealed interface StatValue{
    data class IntValue(val value: Int): StatValue
    data class DoubleValue(val value: Double): StatValue
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