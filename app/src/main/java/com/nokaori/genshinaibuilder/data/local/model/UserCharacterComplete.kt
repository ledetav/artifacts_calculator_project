package com.nokaori.genshinaibuilder.data.local.model

import androidx.room.Embedded
import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserCharacterEntity

data class UserCharacterComplete(
    @Embedded
    val userCharacter: UserCharacterEntity,

    @Embedded(prefix = "char_dict_")
    val characterEntity: CharacterEntity
)