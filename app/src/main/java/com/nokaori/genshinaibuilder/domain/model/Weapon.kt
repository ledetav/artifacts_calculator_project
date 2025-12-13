package com.nokaori.genshinaibuilder.domain.model

enum class Rarity(val stars: Int) {
    THREE_STARS(3),
    FOUR_STARS(4),
    FIVE_STARS(5)
}

data class Weapon(
    val id: Int = 0, // по умолчанию для нового оружия
    val name: String, // название оружия
    val type: WeaponType, // тип оружия
    val rarity: Rarity, // редкость оружия
    val baseAttackLvl1: Int, // базовая атака на уровне 1
    val scalingCurveId: String, // id кривой атаки
    val mainStat: Stat? // подстат 
)

data class UserWeapon(
    val id: Int, // по умолчанию для нового экземпляра оружия у пользователя
    val weapon: Weapon, // оружие
    val level: Int, // уровень оружия
    val ascension: Int, // уровень возвышения
    val refinement: Int, // ранг пробуждения
    val isLocked: Boolean = false
)