package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.entity.ArtifactPieceEntity
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.data.local.model.UserArtifactComplete
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactPiece
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatValue

// Превращаем сложный объект из БД (Инвентарь + Сет + Кусок) в Domain
fun UserArtifactComplete.toDomain(): Artifact {
    return Artifact(
        id = this.userArtifact.id,
        slot = this.userArtifact.slot,
        rarity = Rarity.fromInt(this.userArtifact.rarity),
        setName = this.setEntity.name,

        // Берем имя конкретного куска ("Солнечная реликвия")
        artifactName = this.pieceEntity.name,

        iconUrl = this.pieceEntity.iconUrl,

        level = this.userArtifact.level,
        isLocked = this.userArtifact.isLocked,

        mainStat = Stat(
            type = this.userArtifact.mainStatType,
            value = if (this.userArtifact.mainStatType.isPercentage)
                StatValue.DoubleValue(this.userArtifact.mainStatValue.toDouble())
            else
                StatValue.IntValue(this.userArtifact.mainStatValue.toInt())
        ),

        // Подстаты уже хранятся как List<Stat> благодаря конвертеру
        subStats = this.userArtifact.subStats
    )
}

// Маппер для списка сетов (для фильтров)
fun ArtifactSetEntity.toDomain(
    pieces: List<ArtifactPieceEntity> = emptyList()
): ArtifactSet {
    return ArtifactSet(
        id = this.id,
        name = this.name,
        iconUrl = this.iconUrl,
        rarities = this.rarities.map { Rarity.fromInt(it) },
        bonus2pc = this.bonus2pc,
        bonus4pc = this.bonus4pc,
        pieces = pieces.map { it.toDomain() }
    )
}

fun ArtifactPieceEntity.toDomain(): ArtifactPiece {
    return ArtifactPiece(
        id = this.id,
        name = this.name,
        iconUrl = this.iconUrl,
        slot = this.slot
    )
}