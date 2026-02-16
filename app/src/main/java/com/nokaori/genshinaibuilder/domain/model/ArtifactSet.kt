package com.nokaori.genshinaibuilder.domain.model

data class ArtifactSet(
    val id: Int = 0,
    val name: String,
    val iconUrl: String,
    val rarities: List<Rarity> = emptyList(),
    val bonus2pc: String = "",
    val bonus4pc: String = "",
    val pieces: List<ArtifactPiece> = emptyList()
)

data class ArtifactPiece(
    val id: Int,
    val name: String,
    val iconUrl: String,
    val slot: ArtifactSlot
)