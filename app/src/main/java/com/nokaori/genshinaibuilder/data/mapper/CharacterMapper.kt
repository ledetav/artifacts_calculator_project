package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.local.model.CharacterWithOwnership
import com.nokaori.genshinaibuilder.data.local.model.UserCharacterComplete
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.UserCharacter
import com.nokaori.genshinaibuilder.domain.model.WeaponType

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

// Вспомогательная функция для превращения Entity энциклопедии в Character
private fun CharacterEntity.toDomainModel(): Character {
    return Character(
        id = this.id,
        name = this.name,
        element = this.element,
        weaponType = this.weaponType,
        rarity = this.rarity,
        iconUrl = this.iconUrl,
        isOwned = true
    )
}

// Основной маппер для UserCharacter
fun UserCharacterComplete.toDomain(): UserCharacter {
    return UserCharacter(
        id = this.userCharacter.id,
        character = this.characterEntity.toDomainModel(),
        level = this.userCharacter.level,
        ascension = this.userCharacter.ascension,
        constellation = this.userCharacter.constellation,
        talentNormalLevel = this.userCharacter.talentNormalLevel,
        talentSkillLevel = this.userCharacter.talentSkillLevel,
        talentBurstLevel = this.userCharacter.talentBurstLevel
    )
}