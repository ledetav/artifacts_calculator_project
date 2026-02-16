package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArtifactSetFilterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSetFilterTitleDisplayed() {
        composeTestRule.setContent {
            ArtifactSetFilter(
                selectedArtifactSet = null,
                artifactSetSearchQuery = "",
                isArtifactSetDropdownExpanded = false,
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {}
            )
        }

        composeTestRule.onNodeWithText("Набор артефактов").assertIsDisplayed()
    }

    @Test
    fun testSetFilterPlaceholderDisplayed() {
        composeTestRule.setContent {
            ArtifactSetFilter(
                selectedArtifactSet = null,
                artifactSetSearchQuery = "",
                isArtifactSetDropdownExpanded = false,
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {}
            )
        }

        composeTestRule.onNodeWithText("Выберите набор").assertIsDisplayed()
    }

    @Test
    fun testSetFilterCallsOnSearchQueryChangedWhenSearching() {
        var searchQuery = ""
        composeTestRule.setContent {
            ArtifactSetFilter(
                selectedArtifactSet = null,
                artifactSetSearchQuery = searchQuery,
                isArtifactSetDropdownExpanded = false,
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = { searchQuery = it },
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {}
            )
        }

        composeTestRule.onNodeWithText("Выберите набор").performTextInput("test")
        assert(searchQuery == "test")
    }

    @Test
    fun testSetFilterCallsOnExpandedChangeWhenClicked() {
        var isExpanded = false
        composeTestRule.setContent {
            ArtifactSetFilter(
                selectedArtifactSet = null,
                artifactSetSearchQuery = "",
                isArtifactSetDropdownExpanded = isExpanded,
                filteredArtifactSets = emptyList(),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = { isExpanded = it },
                onClearSelectedArtifactSet = {}
            )
        }

        composeTestRule.onNodeWithText("Выберите набор").performClick()
        assert(isExpanded)
    }

    @Test
    fun testSetFilterDisplaysSelectedSet() {
        val selectedSet = ArtifactSet(1, "Test Set")
        composeTestRule.setContent {
            ArtifactSetFilter(
                selectedArtifactSet = selectedSet,
                artifactSetSearchQuery = "",
                isArtifactSetDropdownExpanded = false,
                filteredArtifactSets = listOf(selectedSet),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {}
            )
        }

        composeTestRule.onNodeWithText("Test Set").assertIsDisplayed()
    }

    @Test
    fun testSetFilterCallsOnClearWhenClearClicked() {
        var clearCalled = false
        val selectedSet = ArtifactSet(1, "Test Set")
        composeTestRule.setContent {
            ArtifactSetFilter(
                selectedArtifactSet = selectedSet,
                artifactSetSearchQuery = "test",
                isArtifactSetDropdownExpanded = false,
                filteredArtifactSets = listOf(selectedSet),
                onArtifactSetSelected = {},
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = { clearCalled = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Очистить").performClick()
        assert(clearCalled)
    }

    @Test
    fun testSetFilterCallsOnSetSelectedWhenSetClicked() {
        var selectedSet: ArtifactSet? = null
        val testSet = ArtifactSet(1, "Test Set")
        composeTestRule.setContent {
            ArtifactSetFilter(
                selectedArtifactSet = null,
                artifactSetSearchQuery = "",
                isArtifactSetDropdownExpanded = true,
                filteredArtifactSets = listOf(testSet),
                onArtifactSetSelected = { selectedSet = it },
                onArtifactSetSearchQueryChanged = {},
                onArtifactSetDropdownExpandedChange = {},
                onClearSelectedArtifactSet = {}
            )
        }

        composeTestRule.onNodeWithText("Test Set").performClick()
        assert(selectedSet == testSet)
    }
}
