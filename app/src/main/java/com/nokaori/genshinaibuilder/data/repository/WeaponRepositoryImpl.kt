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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map

class WeaponRepositoryImpl(
    private val weaponDao: WeaponDao,
    private val userDao: UserDao
) : WeaponRepository {

    override fun getAllWeapons(): Flow<List<Weapon>> {
        return weaponDao.getAllWeapons().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllWeaponsPaged(): Flow<PagingData<Weapon>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { weaponDao.getAllWeaponsPaging() }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override fun getUserWeapons(): Flow<List<UserWeapon>> {
        return userDao.getUserWeaponsComplete().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addUserWeapon(userWeapon: UserWeapon) {
        val entity = UserWeaponEntity(
            id = 0,
            weaponId = userWeapon.weapon.id, // ID из энциклопедии
            level = userWeapon.level,
            ascension = userWeapon.ascension,
            refinement = userWeapon.refinement,
            isLocked = userWeapon.isLocked,
            equippedCharacterId = null
        )
        userDao.insertUserWeapon(entity)
    }
}