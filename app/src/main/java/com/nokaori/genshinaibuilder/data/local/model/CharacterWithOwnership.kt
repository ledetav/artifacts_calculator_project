package com.nokaori.genshinaibuilder.data.local.model

import androidx.room.Embedded
import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity

data class CharacterWithOwnership(
    @Embedded
    val character: CharacterEntity,
    val isOwned: Boolean
)