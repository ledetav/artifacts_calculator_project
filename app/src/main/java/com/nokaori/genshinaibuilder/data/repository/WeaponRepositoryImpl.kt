package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.UserDao
import com.nokaori.genshinaibuilder.data.local.dao.WeaponDao
import com.nokaori.genshinaibuilder.data.local.entity.UserWeaponEntity
import com.nokaori.genshinaibuilder.data.mapper.toDomain
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.SupportedLanguages
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import com.nokaori.genshinaibuilder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import javax.inject.Inject

class WeaponRepositoryImpl @Inject constructor (
    private val weaponDao: WeaponDao,
    private val userDao: UserDao,
    private val themeRepository: SettingsRepository
) : WeaponRepository {
    private val defaultLanguage = SupportedLanguages.EN

    override fun getAllWeapons(): Flow<List<Weapon>> {
        return themeRepository.appLanguage.flatMapLatest { language ->
            weaponDao.getAllWeapons(language).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    override fun getAllWeaponsPaged(): Flow<PagingData<Weapon>> {
        return themeRepository.appLanguage.flatMapLatest { language ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = true
                ),
                pagingSourceFactory = { weaponDao.getAllWeaponsPaging(language) }
            ).flow.map { pagingData ->
                pagingData.map { entity -> entity.toDomain() }
            }
        }
    }

    override suspend fun getAllWeaponUrls(): List<String> {
        val language = themeRepository.appLanguage.first()
        return weaponDao.getAllWeaponUrls(language)
    }

    override fun getUserWeapons(): Flow<List<UserWeapon>> {
        return themeRepository.appLanguage.flatMapLatest { language ->
            userDao.getUserWeaponsComplete(language).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    override suspend fun addUserWeapon(userWeapon: UserWeapon) {
        val entity = UserWeaponEntity(
            id = 0,
            weaponId = userWeapon.weapon.id,
            level = userWeapon.level,
            ascension = userWeapon.ascension,
            refinement = userWeapon.refinement,
            isLocked = userWeapon.isLocked,
            equippedCharacterId = null
        )
        userDao.insertUserWeapon(entity)
    }

    override suspend fun getWeaponDetails(weaponId: Int): Weapon {
        val language = themeRepository.appLanguage.first()
        val weaponEntity = weaponDao.getWeaponById(weaponId, language)
            ?: throw IllegalStateException("Weapon not found")

        val refinementEntity = weaponDao.getWeaponRefinement(weaponId, language)

        return weaponEntity.toDomain(refinementEntity)
    }
}
