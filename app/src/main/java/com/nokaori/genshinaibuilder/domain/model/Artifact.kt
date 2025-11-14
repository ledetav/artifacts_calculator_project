package com.nokaori.genshinaibuilder.domain.model

data class Artifact(
    val id: Int = 0, // по умолчанию для нового артефакта
    val slot: ArtifactSlot, // цветок, перо, часы, кубок, корона
    val rarity: ArtifactRarity,
    val setName: String, // название сета артефактов
    val artifactName: String,
    val level: Int, // уроень прокачки
    val mainStat: Stat,
    val subStats: List<Stat>
)

enum class ArtifactRarity(val stars: Int) {
    THREE_STARS(3),
    FOUR_STARS(4),
    FIVE_STARS(5)
}

enum class ArtifactSlot(){
    FLOWER_OF_LIFE,
    PLUME_OF_DEATH,
    SANDS_OF_EON,
    GOBLET_OF_EONOTHEM,
    CIRCLET_OF_LOGOS
}