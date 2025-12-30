package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.StatType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArtifactMainStatFilterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMainStatFilterTitleDisplayed() {
        composeTestRule.setContent {
            ArtifactMainStatFilter(
                selectedArtifactMainStat = null,
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Главный статус артефакта").assertIsDisplayed()
    }

    @Test
    fun testMainStatFilterPlaceholderDisplayed() {
        composeTestRule.setContent {
            ArtifactMainStatFilter(
                selectedArtifactMainStat = null,
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Выберите статус").assertIsDisplayed()
    }

    @Test
    fun testMainStatFilterCallsOnStatSelectedWhenStatClicked() {
        var selectedStat: StatType? = null
        composeTestRule.setContent {
            ArtifactMainStatFilter(
                selectedArtifactMainStat = null,
                onArtifactMainStatSelected = { selectedStat = it },
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("Выберите статус").performClick()
        composeTestRule.onNodeWithText("HP").performClick()
        assert(selectedStat == StatType.HP)
    }

    @Test
    fun testMainStatFilterDisplaysSelectedStat() {
        composeTestRule.setContent {
            ArtifactMainStatFilter(
                selectedArtifactMainStat = StatType.ATK,
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = {}
            )
        }

        composeTestRule.onNodeWithText("ATK").assertIsDisplayed()
    }

    @Test
    fun testMainStatFilterCallsOnClearWhenClearClicked() {
        var clearCalled = false
        composeTestRule.setContent {
            ArtifactMainStatFilter(
                selectedArtifactMainStat = StatType.ATK,
                onArtifactMainStatSelected = {},
                onClearSelectedArtifactMainStat = { clearCalled = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Очистить").performClick()
        assert(clearCalled)
    }
}
