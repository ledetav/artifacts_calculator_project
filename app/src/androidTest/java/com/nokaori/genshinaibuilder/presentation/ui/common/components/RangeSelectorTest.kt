package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RangeSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testRangeSelectorTitleDisplayed() {
        composeTestRule.setContent {
            RangeSelector(
                title = "Level Range",
                range = 0f..20f,
                valueRange = 0f..20f,
                keyValues = listOf(0, 4, 8, 12, 16, 20),
                fromLabel = "From",
                toLabel = "To",
                onRangeChanged = {},
                onManualInput = {}
            )
        }

        composeTestRule.onNodeWithText("Level Range").assertIsDisplayed()
    }

    @Test
    fun testRangeSelectorCallsOnRangeChangedWhenDragged() {
        var changedRange: ClosedFloatingPointRange<Float>? = null
        composeTestRule.setContent {
            RangeSelector(
                title = "Level Range",
                range = 0f..20f,
                valueRange = 0f..20f,
                keyValues = listOf(0, 4, 8, 12, 16, 20),
                fromLabel = "From",
                toLabel = "To",
                onRangeChanged = { changedRange = it },
                onManualInput = {}
            )
        }

        composeTestRule.onAllNodesWithRole(Role.Slider).onFirst().performTouchInput { swipeRight() }
        assert(changedRange != null)
    }

    @Test
    fun testRangeSelectorManualInputFields() {
        composeTestRule.setContent {
            RangeSelector(
                title = "Level Range",
                range = 5f..15f,
                valueRange = 0f..20f,
                keyValues = listOf(0, 4, 8, 12, 16, 20),
                fromLabel = "From",
                toLabel = "To",
                onRangeChanged = {},
                onManualInput = {}
            )
        }

        composeTestRule.onNodeWithText("From").assertIsDisplayed()
        composeTestRule.onNodeWithText("To").assertIsDisplayed()
    }

    @Test
    fun testRangeSelectorCallsOnManualInputWhenCommitted() {
        var manualInputCalled = false
        composeTestRule.setContent {
            RangeSelector(
                title = "Level Range",
                range = 0f..20f,
                valueRange = 0f..20f,
                keyValues = listOf(0, 4, 8, 12, 16, 20),
                fromLabel = "From",
                toLabel = "To",
                onRangeChanged = {},
                onManualInput = { _, _ -> manualInputCalled = true }
            )
        }

        composeTestRule.onNodeWithText("From").performTextInput("5")
        composeTestRule.onNodeWithText("To").performTextInput("15")
        assert(manualInputCalled)
    }

    @Test
    fun testRangeSelectorMinRange() {
        composeTestRule.setContent {
            RangeSelector(
                title = "Level Range",
                range = 0f..0f,
                valueRange = 0f..20f,
                keyValues = listOf(0, 4, 8, 12, 16, 20),
                fromLabel = "From",
                toLabel = "To",
                onRangeChanged = {},
                onManualInput = {}
            )
        }

        composeTestRule.onNodeWithText("Level Range").assertIsDisplayed()
    }

    @Test
    fun testRangeSelectorMaxRange() {
        composeTestRule.setContent {
            RangeSelector(
                title = "Level Range",
                range = 0f..20f,
                valueRange = 0f..20f,
                keyValues = listOf(0, 4, 8, 12, 16, 20),
                fromLabel = "From",
                toLabel = "To",
                onRangeChanged = {},
                onManualInput = {}
            )
        }

        composeTestRule.onNodeWithText("Level Range").assertIsDisplayed()
    }
}
