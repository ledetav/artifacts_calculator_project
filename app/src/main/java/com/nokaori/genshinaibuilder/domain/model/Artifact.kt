package com.nokaori.genshinaibuilder.domain.model

data class Artifact(
    val id: Int = 0, 
    val slot: ArtifactSlot, 
    val rarity: Rarity,
    val setName: String,
    val artifactName: String,
    val iconUrl: String,
    val level: Int,
    val mainStat: Stat,
    val subStats: List<Stat>,
    val isLocked: Boolean = false
)

enum class ArtifactSlot(){
    FLOWER_OF_LIFE,
    PLUME_OF_DEATH,
    SANDS_OF_EON,
    GOBLET_OF_EONOTHEM,
    CIRCLET_OF_LOGOS
}