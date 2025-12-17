package com.nokaori.genshinaibuilder.domain.model

data class Character(
    val id: Int, 
    val name: String,
    val element: Element,
    val weaponType: WeaponType,
    val rarity: Rarity,
    val iconUrl: String, 
    val isOwned: Boolean = false 
)