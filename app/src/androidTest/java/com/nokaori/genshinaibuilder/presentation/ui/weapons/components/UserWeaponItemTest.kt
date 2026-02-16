package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.StatValue
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserWeaponItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testWeaponItemDisplaysName() {
        val userWeapon = createTestUserWeapon("Test Sword")
        composeTestRule.setContent {
            UserWeaponItem(userWeapon = userWeapon)
        }

        composeTestRule.onNodeWithText("Test Sword [R1]").assertIsDisplayed()
    }

    @Test
    fun testWeaponItemDisplaysLevel() {
        val userWeapon = createTestUserWeapon("Test Sword", level = 90)
        composeTestRule.setContent {
            UserWeaponItem(userWeapon = userWeapon)
        }

        composeTestRule.onNodeWithText("Lv. 90").assertIsDisplayed()
    }

    @Test
    fun testWeaponItemDisplaysRarity() {
        val userWeapon = createTestUserWeapon("Test Sword", rarity = Rarity.FIVE_STAR)
        composeTestRule.setContent {
            UserWeaponItem(userWeapon = userWeapon)
        }

        composeTestRule.onNodeWithText("⭐⭐⭐⭐⭐").assertIsDisplayed()
    }

    @Test
    fun testWeaponItemDisplaysType() {
        val userWeapon = createTestUserWeapon("Test Sword", type = WeaponType.SWORD)
        composeTestRule.setContent {
            UserWeaponItem(userWeapon = userWeapon)
        }

        composeTestRule.onNodeWithText("Меч").assertIsDisplayed()
    }

    @Test
    fun testWeaponItemDisplaysAttack() {
        val userWeapon = createTestUserWeapon("Test Sword")
        composeTestRule.setContent {
            UserWeaponItem(userWeapon = userWeapon)
        }

        composeTestRule.onNodeWithText("ATK: 100").assertIsDisplayed()
    }

    @Test
    fun testWeaponItemDisplaysRefinement() {
        val userWeapon = createTestUserWeapon("Test Sword", refinement = 5)
        composeTestRule.setContent {
            UserWeaponItem(userWeapon = userWeapon)
        }

        composeTestRule.onNodeWithText("Test Sword [R5]").assertIsDisplayed()
    }

    private fun createTestUserWeapon(
        name: String,
        level: Int = 90,
        rarity: Rarity = Rarity.FIVE_STAR,
        type: WeaponType = WeaponType.SWORD,
        refinement: Int = 1
    ): UserWeapon {
        return UserWeapon(
            id = 1,
            weapon = Weapon(
                id = 1,
                name = name,
                type = type,
                rarity = rarity,
                mainStat = Stat(
                    type = StatType.ATK,
                    value = StatValue.IntValue(100)
                )
            ),
            level = level,
            ascension = 6,
            refinement = refinement
        )
    }
}
