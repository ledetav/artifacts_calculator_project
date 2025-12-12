package com.nokaori.genshinaibuilder.domain.model

// Снимок оружия
data class WeaponSnapshot(
    val weaponId: Int, // ID из энциклопедии
    val name: String,
    val level: Int,
    val refinement: Int,
    val ascension: Int
)

// Снимок артефакта
data class ArtifactSnapshot(
    val artifactId: Int, // ID из энциклопедии
    val setId: Int,
    val slot: ArtifactSlot,
    val rarity: Int,
    val level: Int,
    val mainStat: Stat,
    val subStats: List<Stat> 
)