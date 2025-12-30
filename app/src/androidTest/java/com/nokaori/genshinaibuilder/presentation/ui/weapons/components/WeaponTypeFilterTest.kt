package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import com.nokaori.genshinaibuilder.presentation.ui.common.Orientation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeaponTypeFilterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testWeaponTypeFilterTitleDisplayed() {
        composeTestRule.setContent {
            WeaponTypeFilter(
                selectedWeaponTypes = emptySet(),
                onWeaponTypeSelected = {}
            )
        }

        composeTestRule.onNodeWithText("Тип оружия").assertIsDisplayed()
    }

    @Test
    fun testWeaponTypeFilterCallsOnClickWhenTypeClicked() {
        var clickedType: WeaponType? = null
        composeTestRule.setContent {
            WeaponTypeFilter(
                selectedWeaponTypes = emptySet(),
                onWeaponTypeSelected = { clickedType = it }
            )
        }

        composeTestRule.onNodeWithContentDescription("Меч").performClick()
        assert(clickedType == WeaponType.SWORD)
    }

    @Test
    fun testWeaponTypeFilterMultipleSelections() {
        var selectedTypes = setOf<WeaponType>()
        composeTestRule.setContent {
            WeaponTypeFilter(
                selectedWeaponTypes = selectedTypes,
                onWeaponTypeSelected = {
                    selectedTypes = if (selectedTypes.contains(it)) {
                        selectedTypes - it
                    } else {
                        selectedTypes + it
                    }
                }
            )
        }

        composeTestRule.onNodeWithContentDescription("Меч").performClick()
        composeTestRule.onNodeWithContentDescription("Копье").performClick()
        assert(selectedTypes.size == 2)
    }

    @Test
    fun testWeaponTypeFilterHorizontalOrientation() {
        composeTestRule.setContent {
            WeaponTypeFilter(
                selectedWeaponTypes = emptySet(),
                onWeaponTypeSelected = {},
                orientation = Orientation.HORIZONTAL
            )
        }

        composeTestRule.onNodeWithText("Тип оружия").assertIsDisplayed()
    }

    @Test
    fun testWeaponTypeFilterVerticalOrientation() {
        composeTestRule.setContent {
            WeaponTypeFilter(
                selectedWeaponTypes = emptySet(),
                onWeaponTypeSelected = {},
                orientation = Orientation.VERTICAL
            )
        }

        composeTestRule.onNodeWithText("Тип оружия").assertIsDisplayed()
    }
}
