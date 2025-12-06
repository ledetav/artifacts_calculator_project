package com.nokaori.genshinaibuilder.domain.model

data class Character(
    val id: Int, 
    val name: String,
    val element: Element,
    val weaponType: WeaponType, // Используем уже существующий Enum из Weapon.kt
    val rarity: Int, // 4 или 5
    val iconUrl: String, // Ссылка или имя ресурса
    val isOwned: Boolean = false // По умолчанию персонажа нет
)