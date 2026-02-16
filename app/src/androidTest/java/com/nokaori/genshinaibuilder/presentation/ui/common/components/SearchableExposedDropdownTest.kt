package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchableExposedDropdownTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDropdownLabelDisplayed() {
        composeTestRule.setContent {
            SearchableExposedDropdown(
                label = "Select Item",
                searchQuery = "",
                onSearchQueryChange = {},
                isExpanded = false,
                onExpandedChange = {},
                onDismiss = {},
                selectedValueText = "",
                onClear = {}
            ) {
                DropdownMenuItem(text = { Text("Item 1") }, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Select Item").assertIsDisplayed()
    }

    @Test
    fun testDropdownSearchQueryUpdates() {
        var searchQuery = ""
        composeTestRule.setContent {
            SearchableExposedDropdown(
                label = "Select Item",
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isExpanded = false,
                onExpandedChange = {},
                onDismiss = {},
                selectedValueText = "",
                onClear = {}
            ) {
                DropdownMenuItem(text = { Text("Item 1") }, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Select Item").performTextInput("test")
        assert(searchQuery == "test")
    }

    @Test
    fun testDropdownExpandsOnClick() {
        var isExpanded = false
        composeTestRule.setContent {
            SearchableExposedDropdown(
                label = "Select Item",
                searchQuery = "",
                onSearchQueryChange = {},
                isExpanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                onDismiss = {},
                selectedValueText = "",
                onClear = {}
            ) {
                DropdownMenuItem(text = { Text("Item 1") }, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Select Item").performClick()
        assert(isExpanded)
    }

    @Test
    fun testDropdownClearButtonDisplayedWhenSearchQueryNotEmpty() {
        composeTestRule.setContent {
            SearchableExposedDropdown(
                label = "Select Item",
                searchQuery = "test",
                onSearchQueryChange = {},
                isExpanded = false,
                onExpandedChange = {},
                onDismiss = {},
                selectedValueText = "",
                onClear = {}
            ) {
                DropdownMenuItem(text = { Text("Item 1") }, onClick = {})
            }
        }

        composeTestRule.onNodeWithContentDescription("Очистить").assertIsDisplayed()
    }

    @Test
    fun testDropdownClearButtonCallsOnClear() {
        var clearCalled = false
        composeTestRule.setContent {
            SearchableExposedDropdown(
                label = "Select Item",
                searchQuery = "test",
                onSearchQueryChange = {},
                isExpanded = false,
                onExpandedChange = {},
                onDismiss = {},
                selectedValueText = "",
                onClear = { clearCalled = true }
            ) {
                DropdownMenuItem(text = { Text("Item 1") }, onClick = {})
            }
        }

        composeTestRule.onNodeWithContentDescription("Очистить").performClick()
        assert(clearCalled)
    }

    @Test
    fun testDropdownDisplaysSelectedValue() {
        composeTestRule.setContent {
            SearchableExposedDropdown(
                label = "Select Item",
                searchQuery = "",
                onSearchQueryChange = {},
                isExpanded = false,
                onExpandedChange = {},
                onDismiss = {},
                selectedValueText = "Selected Item",
                onClear = {}
            ) {
                DropdownMenuItem(text = { Text("Item 1") }, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Selected Item").assertIsDisplayed()
    }
}
