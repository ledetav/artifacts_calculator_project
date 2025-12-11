package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.local.model.CharacterWithOwnership
import com.nokaori.genshinaibuilder.data.local.model.UserCharacterComplete
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.UserCharacter

// 1. Для списка энциклопедии (с галочкой "Есть/Нет")
fun CharacterWithOwnership.toDomain(): Character {
    return this.character.toDomain(isOwned = this.isOwned)
}

// 2. Вспомогательный: Entity + флаг -> Domain Character
fun CharacterEntity.toDomain(isOwned: Boolean): Character {
    return Character(
        id = this.id,
        name = this.name,
        element = this.element,
        weaponType = this.weaponType,
        rarity = this.rarity,
        iconUrl = this.iconUrl,
        isOwned = isOwned
    )
}

// 3. Для деталей прокачки (UserCharacter)
fun UserCharacterComplete.toDomain(): UserCharacter {
    return UserCharacter(
        id = this.userCharacter.id, // ID в инвентаре
        // Используем вспомогательный метод (isOwned всегда true, т.к. это наш перс)
        character = this.characterEntity.toDomain(isOwned = true),

        level = this.userCharacter.level,
        ascension = this.userCharacter.ascension,
        constellation = this.userCharacter.constellation,

        talentNormalLevel = this.userCharacter.talentNormalLevel,
        talentSkillLevel = this.userCharacter.talentSkillLevel,
        talentBurstLevel = this.userCharacter.talentBurstLevel
    )
}