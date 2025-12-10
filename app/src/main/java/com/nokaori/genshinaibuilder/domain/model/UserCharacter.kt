package com.nokaori.genshinaibuilder.domain.model

data class UserCharacter(
    val id: Int, // ID записи в инвентаре
    val character: Character,

    // Прогресс
    val level: Int,
    val ascension: Int,
    val constellation: Int,

    // Таланты
    val talentNormalLevel: Int,
    val talentSkillLevel: Int,
    val talentBurstLevel: Int
)