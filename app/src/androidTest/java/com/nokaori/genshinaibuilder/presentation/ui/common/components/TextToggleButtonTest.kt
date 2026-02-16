package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TextToggleButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTextToggleButtonDisplaysText() {
        composeTestRule.setContent {
            TextToggleButton(text = "Select", isSelected = false, onClick = {})
        }

        composeTestRule.onNodeWithText("Select").assertIsDisplayed()
    }

    @Test
    fun testTextToggleButtonCallsOnClickWhenClicked() {
        var clickCount = 0
        composeTestRule.setContent {
            TextToggleButton(text = "Select", isSelected = false, onClick = { clickCount++ })
        }

        composeTestRule.onNodeWithText("Select").performClick()
        assert(clickCount == 1)
    }

    @Test
    fun testTextToggleButtonSelectedState() {
        composeTestRule.setContent {
            TextToggleButton(text = "Select", isSelected = true, onClick = {})
        }

        composeTestRule.onNodeWithText("Select").assertIsDisplayed()
    }

    @Test
    fun testTextToggleButtonUnselectedState() {
        composeTestRule.setContent {
            TextToggleButton(text = "Select", isSelected = false, onClick = {})
        }

        composeTestRule.onNodeWithText("Select").assertIsDisplayed()
    }

    @Test
    fun testTextToggleButtonMultipleClicks() {
        var clickCount = 0
        composeTestRule.setContent {
            TextToggleButton(text = "Select", isSelected = false, onClick = { clickCount++ })
        }

        composeTestRule.onNodeWithText("Select").performClick()
        composeTestRule.onNodeWithText("Select").performClick()
        composeTestRule.onNodeWithText("Select").performClick()
        assert(clickCount == 3)
    }
}
