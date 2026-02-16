package com.nokaori.genshinaibuilder.data.mapper

import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponRefinementEntity
import com.nokaori.genshinaibuilder.data.local.model.UserWeaponComplete
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.WeaponRefinement

// Энциклопедия -> Domain
fun WeaponEntity.toDomain(
    refinementEntity: WeaponRefinementEntity? = null
): Weapon {
    return Weapon(
        id = this.id,
        name = this.name,
        type = this.type,
        rarity = Rarity.fromInt(this.rarity),
        baseAttackLvl1 = this.baseAtkLvl1.toInt(),
        scalingCurveId = this.atkCurveId,
        mainStat = if (this.subStatType != null && this.subStatBaseValue != null) {
            Stat(
                type = this.subStatType,
                value = if (this.subStatType.isPercentage)
                    StatValue.DoubleValue(this.subStatBaseValue.toDouble())
                else
                    StatValue.IntValue(this.subStatBaseValue.toInt())
            )
        } else null,
        iconUrl = this.iconUrl,

        refinement = refinementEntity?.let {
            WeaponRefinement(
                passiveName = it.passiveName,
                descriptions = it.descriptions
            )
        }
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