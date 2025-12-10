package com.nokaori.genshinaibuilder.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserArtifactEntity

data class UserArtifactWithSet(
    @Embedded val userArtifact: UserArtifactEntity,

    @Relation(
        parentColumn = "set_id",
        entityColumn = "id"
    )
    val setEntity: ArtifactSetEntity
)