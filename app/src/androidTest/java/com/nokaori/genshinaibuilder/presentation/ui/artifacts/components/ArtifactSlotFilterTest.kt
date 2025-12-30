package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.presentation.ui.common.Orientation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArtifactSlotFilterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSlotFilterTitleDisplayed() {
        composeTestRule.setContent {
            ArtifactSlotFilter(
                selectedArtifactSlots = emptySet(),
                onArtifactSlotClicked = {}
            )
        }

        composeTestRule.onNodeWithText("Слот артефакта").assertIsDisplayed()
    }

    @Test
    fun testSlotFilterCallsOnClickWhenSlotClicked() {
        var clickedSlot: ArtifactSlot? = null
        composeTestRule.setContent {
            ArtifactSlotFilter(
                selectedArtifactSlots = emptySet(),
                onArtifactSlotClicked = { clickedSlot = it }
            )
        }

        composeTestRule.onNodeWithContentDescription("Цветок жизни").performClick()
        assert(clickedSlot == ArtifactSlot.FLOWER)
    }

    @Test
    fun testSlotFilterMultipleSelections() {
        var selectedSlots = setOf<ArtifactSlot>()
        composeTestRule.setContent {
            ArtifactSlotFilter(
                selectedArtifactSlots = selectedSlots,
                onArtifactSlotClicked = {
                    selectedSlots = if (selectedSlots.contains(it)) {
                        selectedSlots - it
                    } else {
                        selectedSlots + it
                    }
                }
            )
        }

        composeTestRule.onNodeWithContentDescription("Цветок жизни").performClick()
        composeTestRule.onNodeWithContentDescription("Перо").performClick()
        assert(selectedSlots.size == 2)
    }

    @Test
    fun testSlotFilterHorizontalOrientation() {
        composeTestRule.setContent {
            ArtifactSlotFilter(
                selectedArtifactSlots = emptySet(),
                onArtifactSlotClicked = {},
                orientation = Orientation.HORIZONTAL
            )
        }

        composeTestRule.onNodeWithText("Слот артефакта").assertIsDisplayed()
    }

    @Test
    fun testSlotFilterVerticalOrientation() {
        composeTestRule.setContent {
            ArtifactSlotFilter(
                selectedArtifactSlots = emptySet(),
                onArtifactSlotClicked = {},
                orientation = Orientation.VERTICAL
            )
        }

        composeTestRule.onNodeWithText("Слот артефакта").assertIsDisplayed()
    }
}
