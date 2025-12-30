package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.StatType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeaponMainStatFilterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMainStatFilterTitleDisplayed() {
        composeTestRule.setContent {
            WeaponMainStatFilter(
                selectedMainStat = null,
                onMainStatSelected = {},
                onClearSelection = {}
            )
        }

        composeTestRule.onNodeWithText("Главный статус оружия").assertIsDisplayed()
    }

    @Test
    fun testMainStatFilterPlaceholderDisplayed() {
        composeTestRule.setContent {
            WeaponMainStatFilter(
                selectedMainStat = null,
                onMainStatSelected = {},
                onClearSelection = {}
            )
        }

        composeTestRule.onNodeWithText("Выберите статус").assertIsDisplayed()
    }

    @Test
    fun testMainStatFilterCallsOnStatSelectedWhenStatClicked() {
        var selectedStat: StatType? = null
        composeTestRule.setContent {
            WeaponMainStatFilter(
                selectedMainStat = null,
                onMainStatSelected = { selectedStat = it },
                onClearSelection = {}
            )
        }

        composeTestRule.onNodeWithText("Выберите статус").performClick()
        composeTestRule.onNodeWithText("ATK").performClick()
        assert(selectedStat == StatType.ATK)
    }

    @Test
    fun testMainStatFilterDisplaysSelectedStat() {
        composeTestRule.setContent {
            WeaponMainStatFilter(
                selectedMainStat = StatType.ATK,
                onMainStatSelected = {},
                onClearSelection = {}
            )
        }

        composeTestRule.onNodeWithText("ATK").assertIsDisplayed()
    }

    @Test
    fun testMainStatFilterCallsOnClearWhenClearClicked() {
        var clearCalled = false
        composeTestRule.setContent {
            WeaponMainStatFilter(
                selectedMainStat = StatType.ATK,
                onMainStatSelected = {},
                onClearSelection = { clearCalled = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Очистить").performClick()
        assert(clearCalled)
    }
}
