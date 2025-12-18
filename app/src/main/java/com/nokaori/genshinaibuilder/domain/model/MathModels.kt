package com.nokaori.genshinaibuilder.domain.model

data class StatCurve(
    val id: String,
    val points: Map<Int, Float>
)

data class CharacterPromotion(
    val ascensionLevel: Int,
    val addHp: Float,
    val addAtk: Float,
    val addDef: Float,
    val ascensionStatValue: Float
)

data class CharacterStatsResult(
    val maxHp: Float,
    val atk: Float,
    val def: Float,
    val ascensionStatType: StatType,
    val ascensionStatValue: Float
)