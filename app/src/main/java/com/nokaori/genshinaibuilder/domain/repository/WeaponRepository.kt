package com.nokaori.genshinaibuilder.domain.repository

import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import kotlinx.coroutines.flow.Flow

interface WeaponRepository {
    fun getAllWeapons(): Flow<List<Weapon>>
    fun getUserWeapons(): Flow<List<UserWeapon>>
    suspend fun addUserWeapon(userWeapon: UserWeapon)
}