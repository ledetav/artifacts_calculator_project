package com.nokaori.genshinaibuilder.domain.model

data class Weapon(
    val id: Int = 0,
    val name: String,
    val type: WeaponType,
    val rarity: Rarity,
    val baseAttackLvl1: Int, 
    val scalingCurveId: String,
    val mainStat: Stat?,
    val iconUrl: String 
)

data class UserWeapon(
    val id: Int, // по умолчанию для нового экземпляра оружия у пользователя
    val weapon: Weapon, // оружие
    val level: Int, // уровень оружия
    val ascension: Int, // уровень возвышения
    val refinement: Int, // ранг пробуждения
    val isLocked: Boolean = false
)