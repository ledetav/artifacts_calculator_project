package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.data.local.model.UserArtifactComplete
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactRarity
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatValue

fun UserArtifactComplete.toDomain(): Artifact {
    return Artifact(
        id = this.userArtifact.id,
        slot = this.userArtifact.slot,
        rarity = when (this.userArtifact.rarity) {
            5 -> ArtifactRarity.FIVE_STARS
            4 -> ArtifactRarity.FOUR_STARS
            else -> ArtifactRarity.THREE_STARS
        },
        setName = this.setEntity.name,
        artifactName = this.pieceEntity.name,
        level = this.userArtifact.level,
        isLocked = this.userArtifact.isLocked,
        mainStat = Stat(
            type = this.userArtifact.mainStatType,
            value = if (this.userArtifact.mainStatType.isPercentage)
                StatValue.DoubleValue(this.userArtifact.mainStatValue.toDouble())
            else
                StatValue.IntValue(this.userArtifact.mainStatValue.toInt())
        ),
        subStats = this.userArtifact.subStats
    )
}

// Превращаем Entity сета в Domain модель сета (для фильтров)
fun ArtifactSetEntity.toDomain(): ArtifactSet {
    return ArtifactSet(
        name = this.name
        // иконку UI подтягивает сам через YattaAssets по имени
    )
}