package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.presentation.ui.weapons.data.WeaponFilterState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeaponFilterDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testFilterDialogTitleDisplayed() {
        composeTestRule.setContent {
            WeaponFilterDialog(
                weaponFilterState = WeaponFilterState(),
                areWeaponFiltersChanged = false,
                onDismiss = {},
                onApply = {},
                onReset = {},
                onWeaponTypeSelected = {},
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onMainStatSelected = {},
                onClearMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Фильтр").assertIsDisplayed()
    }

    @Test
    fun testResetButtonDisplayed() {
        composeTestRule.setContent {
            WeaponFilterDialog(
                weaponFilterState = WeaponFilterState(),
                areWeaponFiltersChanged = false,
                onDismiss = {},
                onApply = {},
                onReset = {},
                onWeaponTypeSelected = {},
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onMainStatSelected = {},
                onClearMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Сброс").assertIsDisplayed()
    }

    @Test
    fun testApplyButtonDisplayed() {
        composeTestRule.setContent {
            WeaponFilterDialog(
                weaponFilterState = WeaponFilterState(),
                areWeaponFiltersChanged = false,
                onDismiss = {},
                onApply = {},
                onReset = {},
                onWeaponTypeSelected = {},
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onMainStatSelected = {},
                onClearMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Применить").assertIsDisplayed()
    }

    @Test
    fun testApplyButtonDisabledWhenNoChanges() {
        composeTestRule.setContent {
            WeaponFilterDialog(
                weaponFilterState = WeaponFilterState(),
                areWeaponFiltersChanged = false,
                onDismiss = {},
                onApply = {},
                onReset = {},
                onWeaponTypeSelected = {},
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onMainStatSelected = {},
                onClearMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Применить").assertIsNotEnabled()
    }

    @Test
    fun testApplyButtonEnabledWhenChangesExist() {
        composeTestRule.setContent {
            WeaponFilterDialog(
                weaponFilterState = WeaponFilterState(),
                areWeaponFiltersChanged = true,
                onDismiss = {},
                onApply = {},
                onReset = {},
                onWeaponTypeSelected = {},
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onMainStatSelected = {},
                onClearMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Применить").assertIsEnabled()
    }

    @Test
    fun testResetButtonCallsOnReset() {
        var resetCalled = false
        composeTestRule.setContent {
            WeaponFilterDialog(
                weaponFilterState = WeaponFilterState(),
                areWeaponFiltersChanged = false,
                onDismiss = {},
                onApply = {},
                onReset = { resetCalled = true },
                onWeaponTypeSelected = {},
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onMainStatSelected = {},
                onClearMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Сброс").performClick()
        assert(resetCalled)
    }

    @Test
    fun testApplyButtonCallsOnApply() {
        var applyCalled = false
        composeTestRule.setContent {
            WeaponFilterDialog(
                weaponFilterState = WeaponFilterState(),
                areWeaponFiltersChanged = true,
                onDismiss = {},
                onApply = { applyCalled = true },
                onReset = {},
                onWeaponTypeSelected = {},
                onWeaponLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onMainStatSelected = {},
                onClearMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Применить").performClick()
        assert(applyCalled)
    }
}
