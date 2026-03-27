package com.nokaori.genshinaibuilder.data.local.model

import androidx.room.Embedded
import com.nokaori.genshinaibuilder.data.local.entity.UserWeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity

data class UserWeaponComplete(
    @Embedded
    val userWeapon: UserWeaponEntity,

    @Embedded(prefix = "weapon_dict_")
    val weaponEntity: WeaponEntity
)