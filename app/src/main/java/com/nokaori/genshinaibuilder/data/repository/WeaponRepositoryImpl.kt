package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import com.nokaori.genshinaibuilder.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

// Заглушка для репозитория оружия, позже будет Romm

class WeaponRepositoryImpl : WeaponRepository {

    private val _allWeapons = MutableStateFlow<List<Weapon>>(emptyList())
    private val _userWeapons = MutableStateFlow<List<UserWeapon>>(emptyList())

    init {
        _allWeapons.value = createInintilWeaponDatabase()
    }

    override fun getAllWeapons(): Flow<List<Weapon>> = _allWeapons.asStateFlow()

    override fun getUserWeapons(): Flow<List<UserWeapon>> = _userWeapons.asStateFlow()

    override suspend fun addUserWeapon(userWeapon: UserWeapon) {
        _userWeapons.update { currentList ->
            currentList + userWeapon.copy(id = Random.nextInt())
        }
    }

    private fun createInintilWeaponDatabase(): List<Weapon> {
        return listOf(
            Weapon(
                name = "Волчья погибель",
                type = WeaponType.CLAYMORE,
                rarity = Rarity.FIVE_STARS,
                baseAttackLvl1 = 46,
                scalingCurveId = "5_star_high",
                mainStat = Stat(StatType.ATK_PERCENT, Stat.DoubleValue(49.6))
            ),
            Weapon(
                name = "Боевой лук Фавония",
                type = WeaponType.BOW,
                rarity = Rarity.FOUR_STARS,
                baseAttackLvl1 = 41,
                scalingCurveId = "4_star_er",
                mainStat = Stat(StatType.ENERGY_RECHARGE, Stat.DoubleValue(61.3))
            )
        )
    }
}