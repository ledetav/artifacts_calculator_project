package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.UserDao
import com.nokaori.genshinaibuilder.data.local.dao.WeaponDao
import com.nokaori.genshinaibuilder.data.local.entity.UserWeaponEntity
import com.nokaori.genshinaibuilder.data.mapper.toDomain
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeaponRepositoryImpl(
    private val weaponDao: WeaponDao, // Энциклопедия
    private val userDao: UserDao      // Инвентарь
) : WeaponRepository {

    // Энциклопедия (для выбора, какое оружие добавить)
    override fun getAllWeapons(): Flow<List<Weapon>> {
        return weaponDao.getAllWeapons().map { list ->
            list.map { it.toDomain() }
        }
    }

    // Инвентарь пользователя
    override fun getUserWeapons(): Flow<List<UserWeapon>> {
        return userDao.getUserWeaponsComplete().map { list ->
            list.map { it.toDomain() }
        }
    }

    // Добавление оружия в инвентарь
    override suspend fun addUserWeapon(userWeapon: UserWeapon) {
        val newEntity = UserWeaponEntity(
            id = 0, // Auto-generate
            weaponId = userWeapon.weapon.id, // Ссылаемся на ID из энциклопедии
            level = userWeapon.level,
            ascension = userWeapon.ascension,
            refinement = userWeapon.refinement,
            isLocked = userWeapon.isLocked,
            equippedCharacterId = null // Пока никому не выдано
        )
        userDao.insertUserWeapon(newEntity)
    }
}