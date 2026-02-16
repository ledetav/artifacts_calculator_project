package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.UserDao
import com.nokaori.genshinaibuilder.data.local.dao.WeaponDao
import com.nokaori.genshinaibuilder.data.local.entity.UserWeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponRefinementEntity
import com.nokaori.genshinaibuilder.data.local.model.UserWeaponComplete
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class WeaponRepositoryImplTest {
    @Mock
    private lateinit var weaponDao: WeaponDao
    @Mock
    private lateinit var userDao: UserDao

    private lateinit var repository: WeaponRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = WeaponRepositoryImpl(weaponDao, userDao)
    }

    @Test
    fun getAllWeapons_returnsFlowOfWeapons() = runTest {
        val mockWeapon = WeaponEntity(
            id = 1,
            name = "Aqua Simulacra",
            type = WeaponType.BOW,
            rarity = 5,
            baseAtkLvl1 = 542f,
            subStatType = StatType.CRIT_RATE,
            subStatBaseValue = 44.1f,
            atkCurveId = "WEAPON_CURVE_EXP_FAST",
            subStatCurveId = "WEAPON_CURVE_CRIT_RATE",
            iconUrl = "url"
        )

        whenever(weaponDao.getAllWeapons()).thenReturn(flowOf(listOf(mockWeapon)))

        val result = mutableListOf<Weapon>()
        repository.getAllWeapons().collect { result.addAll(it) }

        assertEquals(1, result.size)
    }

    @Test
    fun getAllWeaponUrls_returnsListOfUrls() = runTest {
        val urls = listOf("url1", "url2", "url3")
        whenever(weaponDao.getAllWeaponUrls()).thenReturn(urls)

        val result = repository.getAllWeaponUrls()

        assertEquals(urls, result)
    }

    @Test
    fun getUserWeapons_returnsFlowOfUserWeapons() = runTest {
        val mockUserWeapon = UserWeaponComplete(
            userWeapon = UserWeaponEntity(
                id = 1,
                weaponId = 1,
                level = 90,
                ascension = 6,
                refinement = 1,
                isLocked = false,
                equippedCharacterId = null
            ),
            weaponEntity = WeaponEntity(
                id = 1,
                name = "Aqua Simulacra",
                type = WeaponType.BOW,
                rarity = 5,
                baseAtkLvl1 = 542f,
                subStatType = StatType.CRIT_RATE,
                subStatBaseValue = 44.1f,
                atkCurveId = "WEAPON_CURVE_EXP_FAST",
                subStatCurveId = "WEAPON_CURVE_CRIT_RATE",
                iconUrl = "url"
            )
        )

        whenever(userDao.getUserWeaponsComplete()).thenReturn(flowOf(listOf(mockUserWeapon)))

        val result = mutableListOf<UserWeapon>()
        repository.getUserWeapons().collect { result.addAll(it) }

        assertEquals(1, result.size)
    }

    @Test
    fun addUserWeapon_withValidWeapon_insertsUserWeapon() = runTest {
        val userWeapon = UserWeapon(
            id = 0,
            weapon = Weapon(
                id = 1,
                name = "Aqua Simulacra",
                type = WeaponType.BOW,
                rarity = Rarity.FIVE_STARS,
                baseAttackLvl1 = 542,
                scalingCurveId = "WEAPON_CURVE_EXP_FAST",
                mainStat = Stat(StatType.CRIT_RATE, StatValue.DoubleValue(44.1)),
                iconUrl = "url"
            ),
            level = 90,
            ascension = 6,
            refinement = 1,
            isLocked = false
        )

        repository.addUserWeapon(userWeapon)

        verify(userDao).insertUserWeapon(any())
    }

    @Test
    fun getWeaponDetails_withValidWeaponId_returnsWeapon() = runTest {
        val mockWeapon = WeaponEntity(
            id = 1,
            name = "Aqua Simulacra",
            type = WeaponType.BOW,
            rarity = 5,
            baseAtkLvl1 = 542f,
            subStatType = StatType.CRIT_RATE,
            subStatBaseValue = 44.1f,
            atkCurveId = "WEAPON_CURVE_EXP_FAST",
            subStatCurveId = "WEAPON_CURVE_CRIT_RATE",
            iconUrl = "url"
        )

        whenever(weaponDao.getWeaponById(1)).thenReturn(mockWeapon)
        whenever(weaponDao.getWeaponRefinement(1)).thenReturn(null)

        val result = repository.getWeaponDetails(1)

        assertEquals("Aqua Simulacra", result.name)
    }

    @Test(expected = IllegalStateException::class)
    fun getWeaponDetails_withInvalidWeaponId_throwsException() = runTest {
        whenever(weaponDao.getWeaponById(999)).thenReturn(null)

        repository.getWeaponDetails(999)
    }
}
