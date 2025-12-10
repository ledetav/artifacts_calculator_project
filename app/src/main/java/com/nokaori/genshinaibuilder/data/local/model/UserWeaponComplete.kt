package com.nokaori.genshinaibuilder.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.nokaori.genshinaibuilder.data.local.entity.UserWeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity

data class UserWeaponComplete(
    @Embedded
    val userWeapon: UserWeaponEntity,

    @Relation(
        parentColumn = "weapon_encyclopedia_id",
        entityColumn = "id"
    )
    val weaponEntity: WeaponEntity
)