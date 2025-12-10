package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserArtifactEntity
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactRarity
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet

// Превращаем пару Entity (Артефакт + Сет) в Domain модель
fun UserArtifactEntity.toDomain(setEntity: ArtifactSetEntity): Artifact {
    return Artifact(
        id = this.id,
        slot = this.slot,
        rarity = when (this.rarity) {
            5 -> ArtifactRarity.FIVE_STARS
            4 -> ArtifactRarity.FOUR_STARS
            else -> ArtifactRarity.THREE_STARS // Заглушка, если будут 3*
        },
        setName = setEntity.name, // Имя берем из сета
        artifactName = "Unknown Piece", // не храним имя куска (только слот).
        // Если нужно реальное имя ("Часы пламени..."), нужно джойнить таблицу ArtifactPieceEntity.
        // Пока заглушка.
        level = this.level,
        mainStat = com.nokaori.genshinaibuilder.domain.model.Stat(
            type = this.mainStatType,
            value = if (this.mainStatType.isPercentage)
                com.nokaori.genshinaibuilder.domain.model.StatValue.DoubleValue(this.mainStatValue.toDouble())
            else
                com.nokaori.genshinaibuilder.domain.model.StatValue.IntValue(this.mainStatValue.toInt())
        ),
        subStats = this.subStats
    )
}

// Превращаем Entity сета в Domain модель сета (для фильтров)
fun ArtifactSetEntity.toDomain(): ArtifactSet {
    return ArtifactSet(
        name = this.name
        // иконку UI подтягивает сам через YattaAssets по имени
    )
}