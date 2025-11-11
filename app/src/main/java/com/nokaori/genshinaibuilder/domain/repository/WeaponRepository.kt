package com.nokaori.genshinaibuilder.domain.repository

import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import kotlinx.coroutines.flow.Flow

interface WeaponRepository {
    fun getWeapons(): Flow<List<Weapon>>
    suspend fun addUserWeapon(userWeapon: UserWeapon)
}