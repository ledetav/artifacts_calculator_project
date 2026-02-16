package com.nokaori.genshinaibuilder.domain.repository

import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface WeaponRepository {
    fun getAllWeapons(): Flow<List<Weapon>>
    fun getAllWeaponsPaged(): Flow<PagingData<Weapon>>
    fun getUserWeapons(): Flow<List<UserWeapon>>
    suspend fun addUserWeapon(userWeapon: UserWeapon)
    suspend fun getAllWeaponUrls(): List<String>
    suspend fun getWeaponDetails(weaponId: Int): Weapon
}