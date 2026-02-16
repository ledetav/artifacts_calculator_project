package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LevelSliderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLevelSliderDisplaysCurrentValue() {
        composeTestRule.setContent {
            LevelSlider(value = 10, maxLevel = 20, onValueChange = {})
        }

        composeTestRule.onNodeWithText("Lv. 10").assertIsDisplayed()
    }

    @Test
    fun testLevelSliderDisplaysMaxLevel() {
        composeTestRule.setContent {
            LevelSlider(value = 10, maxLevel = 20, onValueChange = {})
        }

        composeTestRule.onNodeWithText("Max 20").assertIsDisplayed()
    }

    @Test
    fun testLevelSliderCallsOnValueChangeWhenDragged() {
        var changedValue = 0
        composeTestRule.setContent {
            LevelSlider(value = 5, maxLevel = 20, onValueChange = { changedValue = it })
        }

        composeTestRule.onNodeWithRole(Role.Slider).performTouchInput { swipeRight() }
        assert(changedValue > 5)
    }

    @Test
    fun testLevelSliderMinValue() {
        composeTestRule.setContent {
            LevelSlider(value = 0, maxLevel = 20, onValueChange = {})
        }

        composeTestRule.onNodeWithText("Lv. 0").assertIsDisplayed()
    }

    @Test
    fun testLevelSliderMaxValue() {
        composeTestRule.setContent {
            LevelSlider(value = 20, maxLevel = 20, onValueChange = {})
        }

        composeTestRule.onNodeWithText("Lv. 20").assertIsDisplayed()
    }
}
