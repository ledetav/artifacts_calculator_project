package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeaponLevelFilterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testWeaponLevelFilterTitleDisplayed() {
        composeTestRule.setContent {
            WeaponLevelFilter(
                weaponLevelRange = 0f..90f,
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Уровень").assertIsDisplayed()
    }

    @Test
    fun testWeaponLevelFilterCallsOnRangeChangedWhenDragged() {
        var changedRange: ClosedFloatingPointRange<Float>? = null
        composeTestRule.setContent {
            WeaponLevelFilter(
                weaponLevelRange = 0f..90f,
                onWeaponLevelRangeChanged = { changedRange = it },
                onLevelManualInput = { _, _ -> }
            )
        }

        composeTestRule.onAllNodesWithRole(Role.Slider).onFirst().performTouchInput { swipeRight() }
        assert(changedRange != null)
    }

    @Test
    fun testWeaponLevelFilterCallsOnManualInputWhenCommitted() {
        var manualInputCalled = false
        composeTestRule.setContent {
            WeaponLevelFilter(
                weaponLevelRange = 0f..90f,
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> manualInputCalled = true }
            )
        }

        composeTestRule.onNodeWithText("От").performTextInput("20")
        composeTestRule.onNodeWithText("До").performTextInput("80")
        assert(manualInputCalled)
    }

    @Test
    fun testWeaponLevelFilterMinRange() {
        composeTestRule.setContent {
            WeaponLevelFilter(
                weaponLevelRange = 0f..0f,
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Уровень").assertIsDisplayed()
    }

    @Test
    fun testWeaponLevelFilterMaxRange() {
        composeTestRule.setContent {
            WeaponLevelFilter(
                weaponLevelRange = 0f..90f,
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Уровень").assertIsDisplayed()
    }
}
