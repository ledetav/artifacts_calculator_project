package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.model.CharacterWithOwnership
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.WeaponType

fun CharacterWithOwnership.toDomain(): Character {
    return Character(
        id = this.character.id,
        name = this.character.name,
        element = this.character.element,
        weaponType = this.character.weaponType,
        rarity = this.character.rarity,
        iconUrl = this.character.iconUrl,
        isOwned = this.isOwned
    )
}