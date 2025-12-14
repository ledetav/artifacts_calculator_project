package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity
import com.nokaori.genshinaibuilder.data.local.model.UserWeaponComplete
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.Weapon

// Энциклопедия -> Domain
fun WeaponEntity.toDomain(): Weapon {
    return Weapon(
        id = this.id,
        name = this.name,
        type = this.type,
        rarity = when(this.rarity) {
            5 -> Rarity.FIVE_STARS
            4 -> Rarity.FOUR_STARS
            3 -> Rarity.THREE_STARS
            2 -> Rarity.TWO_STARS
            1 -> Rarity.ONE_STAR
            else -> Rarity.UNKNOWN
        },
        baseAttackLvl1 = this.baseAtkLvl1.toInt(),
        scalingCurveId = this.atkCurveId,
        // Собираем стат (если он есть, у 1-2* может не быть)
        mainStat = if (this.subStatType != null && this.subStatBaseValue != null) {
            Stat(
                type = this.subStatType,
                value = if (this.subStatType.isPercentage)
                    StatValue.DoubleValue(this.subStatBaseValue.toDouble())
                else
                    StatValue.IntValue(this.subStatBaseValue.toInt())
            )
        } else null,
        iconUrl = this.iconUrl
    )
}

// Инвентарь -> Domain
fun UserWeaponComplete.toDomain(): UserWeapon {
    return UserWeapon(
        id = this.userWeapon.id,
        weapon = this.weaponEntity.toDomain(),
        level = this.userWeapon.level,
        ascension = this.userWeapon.ascension,
        refinement = this.userWeapon.refinement,
        isLocked = this.userWeapon.isLocked
    )
}