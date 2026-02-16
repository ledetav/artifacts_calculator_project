package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.data.ArtifactFilterState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArtifactFilterDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testFilterDialogTitleDisplayed() {
        composeTestRule.setContent {
            ArtifactFilterDialog(
                artifactFilterState = ArtifactFilterState(),
                areArtifactFiltersChanged = false,
                onDismiss = {},
                onApply = {},
                onReset = {},
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {},
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onArtifactSlotClicked = {},
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Фильтр").assertIsDisplayed()
    }

    @Test
    fun testResetButtonDisplayed() {
        composeTestRule.setContent {
            ArtifactFilterDialog(
                artifactFilterState = ArtifactFilterState(),
                areArtifactFiltersChanged = false,
                onDismiss = {},
                onApply = {},
                onReset = {},
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {},
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onArtifactSlotClicked = {},
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Сброс").assertIsDisplayed()
    }

    @Test
    fun testApplyButtonDisplayed() {
        composeTestRule.setContent {
            ArtifactFilterDialog(
                artifactFilterState = ArtifactFilterState(),
                areArtifactFiltersChanged = false,
                onDismiss = {},
                onApply = {},
                onReset = {},
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {},
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onArtifactSlotClicked = {},
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Применить").assertIsDisplayed()
    }

    @Test
    fun testApplyButtonDisabledWhenNoChanges() {
        composeTestRule.setContent {
            ArtifactFilterDialog(
                artifactFilterState = ArtifactFilterState(),
                areArtifactFiltersChanged = false,
                onDismiss = {},
                onApply = {},
                onReset = {},
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {},
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onArtifactSlotClicked = {},
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Применить").assertIsNotEnabled()
    }

    @Test
    fun testApplyButtonEnabledWhenChangesExist() {
        composeTestRule.setContent {
            ArtifactFilterDialog(
                artifactFilterState = ArtifactFilterState(),
                areArtifactFiltersChanged = true,
                onDismiss = {},
                onApply = {},
                onReset = {},
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {},
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onArtifactSlotClicked = {},
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Применить").assertIsEnabled()
    }

    @Test
    fun testResetButtonCallsOnReset() {
        var resetCalled = false
        composeTestRule.setContent {
            ArtifactFilterDialog(
                artifactFilterState = ArtifactFilterState(),
                areArtifactFiltersChanged = false,
                onDismiss = {},
                onApply = {},
                onReset = { resetCalled = true },
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {},
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onArtifactSlotClicked = {},
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Сброс").performClick()
        assert(resetCalled)
    }

    @Test
    fun testApplyButtonCallsOnApply() {
        var applyCalled = false
        composeTestRule.setContent {
            ArtifactFilterDialog(
                artifactFilterState = ArtifactFilterState(),
                areArtifactFiltersChanged = true,
                onDismiss = {},
                onApply = { applyCalled = true },
                onReset = {},
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {},
                onArtifactLevelRangeChanged = {},
                onLevelManualInput = { _, _ -> },
                onArtifactSlotClicked = {},
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Применить").performClick()
        assert(applyCalled)
    }
}
