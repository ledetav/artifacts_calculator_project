package com.nokaori.genshinaibuilder.data.local.model

import androidx.room.Embedded
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactPieceEntity
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserArtifactEntity

data class UserArtifactComplete(
    // Данные о прокачке
    @Embedded
    val userArtifact: UserArtifactEntity,

    // Данные о Сете
    @Embedded(prefix = "set_")
    val setEntity: ArtifactSetEntity,

    // Данные о конкретном Куске
    @Embedded(prefix = "piece_")
    val pieceEntity: ArtifactPieceEntity
)