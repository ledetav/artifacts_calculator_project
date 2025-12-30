package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArtifactLevelFilterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLevelFilterTitleDisplayed() {
        composeTestRule.setContent {
            ArtifactLevelFilter(
                artifactLevelRange = 0f..20f,
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Уровень").assertIsDisplayed()
    }

    @Test
    fun testLevelFilterCallsOnRangeChangedWhenDragged() {
        var changedRange: ClosedFloatingPointRange<Float>? = null
        composeTestRule.setContent {
            ArtifactLevelFilter(
                artifactLevelRange = 0f..20f,
                onArtifactLevelRangeChanged = { changedRange = it },
                onLevelManualInput = { _, _ -> }
            )
        }

        composeTestRule.onAllNodesWithRole(Role.Slider).onFirst().performTouchInput { swipeRight() }
        assert(changedRange != null)
    }

    @Test
    fun testLevelFilterCallsOnManualInputWhenCommitted() {
        var manualInputCalled = false
        composeTestRule.setContent {
            ArtifactLevelFilter(
                artifactLevelRange = 0f..20f,
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> manualInputCalled = true }
            )
        }

        composeTestRule.onNodeWithText("От").performTextInput("5")
        composeTestRule.onNodeWithText("До").performTextInput("15")
        assert(manualInputCalled)
    }

    @Test
    fun testLevelFilterMinRange() {
        composeTestRule.setContent {
            ArtifactLevelFilter(
                artifactLevelRange = 0f..0f,
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Уровень").assertIsDisplayed()
    }

    @Test
    fun testLevelFilterMaxRange() {
        composeTestRule.setContent {
            ArtifactLevelFilter(
                artifactLevelRange = 0f..20f,
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Уровень").assertIsDisplayed()
    }
}
