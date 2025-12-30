package com.nokaori.genshinaibuilder.presentation.ui.artifacts

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.data.ArtifactFilterState
import com.nokaori.genshinaibuilder.presentation.viewmodel.ArtifactViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class ArtifactScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSearchFieldDisplayed() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            ArtifactScreen(artifactViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Поиск артефактов").assertIsDisplayed()
    }

    @Test
    fun testSearchQueryUpdatesOnInput() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            ArtifactScreen(artifactViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Поиск артефактов").performTextInput("test")
        verify(mockViewModel).onSearchQueryChange("test")
    }

    @Test
    fun testFilterIconClickOpensDialog() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            ArtifactScreen(artifactViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithContentDescription("Фильтр").performClick()
        verify(mockViewModel).onFilterIconClicked()
    }

    @Test
    fun testArtifactListDisplaysItems() {
        val artifacts = listOf(
            createTestArtifact("Artifact 1"),
            createTestArtifact("Artifact 2")
        )
        val mockViewModel = createMockViewModel(artifacts = artifacts)
        composeTestRule.setContent {
            ArtifactScreen(artifactViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Artifact 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Artifact 2").assertIsDisplayed()
    }

    @Test
    fun testEmptyArtifactListDisplaysNoItems() {
        val mockViewModel = createMockViewModel(artifacts = emptyList())
        composeTestRule.setContent {
            ArtifactScreen(artifactViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Artifact 1").assertDoesNotExist()
    }

    private fun createMockViewModel(
        searchQuery: String = "",
        artifacts: List<Artifact> = emptyList(),
        isFilterDialogShown: Boolean = false
    ): ArtifactViewModel {
        return mock {
            on { this.searchQuery }.thenReturn(MutableStateFlow(searchQuery))
            on { searchedArtifacts }.thenReturn(MutableStateFlow(artifacts))
            on { this.isFilterDialogShown }.thenReturn(MutableStateFlow(isFilterDialogShown))
            on { draftArtifactFilterState }.thenReturn(MutableStateFlow(ArtifactFilterState()))
            on { areArtifactFiltersChanged }.thenReturn(MutableStateFlow(false))
            on { filteredArtifactSets }.thenReturn(MutableStateFlow(emptyList()))
        }
    }

    private fun createTestArtifact(name: String): Artifact {
        return Artifact(
            id = 1,
            name = name,
            slot = ArtifactSlot.FLOWER,
            set = ArtifactSet(1, "Test Set"),
            level = 20,
            mainStat = StatType.HP,
            mainStatValue = 100f,
            subStats = emptyList()
        )
    }
}
