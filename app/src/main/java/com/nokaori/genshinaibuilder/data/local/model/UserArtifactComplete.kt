package com.nokaori.genshinaibuilder.data.local.model

import androidx.room.Embedded
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactPieceEntity
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserArtifactEntity

data class UserArtifactComplete(
    // Данные о прокачке (инвентарь)
    @Embedded
    val userArtifact: UserArtifactEntity,

    // Данные о Сете (префикс нужен, т.к. в SQL запросе мы делали "AS set_...")
    @Embedded(prefix = "set_")
    val setEntity: ArtifactSetEntity,

    // Данные о Куске (префикс "piece_")
    @Embedded(prefix = "piece_")
    val pieceEntity: ArtifactPieceEntity
)