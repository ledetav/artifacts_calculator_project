package com.nokaori.genshinaibuilder.domain.model

data class Character(
    val id: Int, 
    val name: String,
    val element: Element,
    val weaponType: WeaponType,
    val rarity: Rarity,
    val iconUrl: String, 
    val isOwned: Boolean = false,
    val baseHp: Float,
    val baseAtk: Float,
    val baseDef: Float,
    val ascensionStatType: StatType,
    val curveId: String,
    val tagsDictionary: Map<String, String> = emptyMap()
)