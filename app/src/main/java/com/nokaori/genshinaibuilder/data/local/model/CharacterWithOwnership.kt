package com.nokaori.genshinaibuilder.data.local.model

import androidx.room.Embedded
import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity

data class CharacterWithOwnership(
    @Embedded
    val character: CharacterEntity,

    // Это поле заполняется результатом "CASE WHEN ... THEN 1 ELSE 0"
    val isOwned: Boolean
)