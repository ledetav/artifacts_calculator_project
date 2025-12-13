package com.nokaori.genshinaibuilder.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserCharacterEntity

data class UserCharacterComplete(
    @Embedded
    val userCharacter: UserCharacterEntity,

    @Relation(
        parentColumn = "character_encyclopedia_id",
        entityColumn = "id"
    )
    val characterEntity: CharacterEntity
)