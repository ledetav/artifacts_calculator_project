package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.ArtifactPieceEntity
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaRelicDetailDto
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot

private const val ASSETS_URL = "https://gi.yatta.moe/assets/UI"

fun YattaRelicDetailDto.toSetEntity(): ArtifactSetEntity {
    val bonuses = this.bonusMap?.entries?.sortedBy { it.key }?.map { it.value } ?: emptyList()
    
    val bonus2pc = bonuses.getOrNull(0) ?: ""
    val bonus4pc = bonuses.getOrNull(1) ?: ""

    return ArtifactSetEntity(
        id = this.id,
        name = this.name,
        rarities = this.rarities ?: emptyList(),
        bonus2pc = bonus2pc,
        bonus4pc = bonus4pc,
        iconUrl = "$ASSETS_URL/${this.icon}.png"
    )
}

fun mapRelicPieces(setId: Int, dto: YattaRelicDetailDto): List<ArtifactPieceEntity> {
    val suitMap = dto.suit ?: return emptyList()
    
    return suitMap.mapNotNull { (key, pieceDto) ->
        val slot = parseYattaArtifactSlot(key) ?: return@mapNotNull null
        
        val pieceId = setId * 10 + (slot.ordinal + 1)

        ArtifactPieceEntity(
            id = pieceId,
            setId = setId,
            slot = slot,
            name = pieceDto.name,
            iconUrl = "$ASSETS_URL/${pieceDto.icon}.png"
        )
    }
}