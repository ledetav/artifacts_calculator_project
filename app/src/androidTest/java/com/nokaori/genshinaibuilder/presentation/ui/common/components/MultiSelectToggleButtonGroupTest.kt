package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.presentation.ui.common.Orientation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiSelectToggleButtonGroupTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMultiSelectGroupTitleDisplayed() {
        composeTestRule.setContent {
            MultiSelectToggleButtonGroup(
                title = "Select Items",
                items = listOf("Item 1", "Item 2", "Item 3"),
                selectedItems = emptySet(),
                onItemClick = {},
                itemContent = { item, _ -> Text(item) }
            )
        }

        composeTestRule.onNodeWithText("Select Items").assertIsDisplayed()
    }

    @Test
    fun testMultiSelectGroupDisplaysAllItems() {
        composeTestRule.setContent {
            MultiSelectToggleButtonGroup(
                title = "Select Items",
                items = listOf("Item 1", "Item 2", "Item 3"),
                selectedItems = emptySet(),
                onItemClick = {},
                itemContent = { item, _ -> Text(item) }
            )
        }

        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item 3").assertIsDisplayed()
    }

    @Test
    fun testMultiSelectGroupCallsOnItemClickWhenItemClicked() {
        var clickedItem: String? = null
        composeTestRule.setContent {
            MultiSelectToggleButtonGroup(
                title = "Select Items",
                items = listOf("Item 1", "Item 2", "Item 3"),
                selectedItems = emptySet(),
                onItemClick = { clickedItem = it },
                itemContent = { item, _ -> Text(item) }
            )
        }

        composeTestRule.onNodeWithText("Item 1").performClick()
        assert(clickedItem == "Item 1")
    }

    @Test
    fun testMultiSelectGroupMultipleSelections() {
        var selectedItems = setOf<String>()
        composeTestRule.setContent {
            MultiSelectToggleButtonGroup(
                title = "Select Items",
                items = listOf("Item 1", "Item 2", "Item 3"),
                selectedItems = selectedItems,
                onItemClick = { 
                    selectedItems = if (selectedItems.contains(it)) {
                        selectedItems - it
                    } else {
                        selectedItems + it
                    }
                },
                itemContent = { item, _ -> Text(item) }
            )
        }

        composeTestRule.onNodeWithText("Item 1").performClick()
        composeTestRule.onNodeWithText("Item 2").performClick()
        assert(selectedItems.size == 2)
    }

    @Test
    fun testMultiSelectGroupHorizontalOrientation() {
        composeTestRule.setContent {
            MultiSelectToggleButtonGroup(
                title = "Select Items",
                items = listOf("Item 1", "Item 2"),
                selectedItems = emptySet(),
                onItemClick = {},
                orientation = Orientation.HORIZONTAL,
                itemContent = { item, _ -> Text(item) }
            )
        }

        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
    }

    @Test
    fun testMultiSelectGroupVerticalOrientation() {
        composeTestRule.setContent {
            MultiSelectToggleButtonGroup(
                title = "Select Items",
                items = listOf("Item 1", "Item 2"),
                selectedItems = emptySet(),
                onItemClick = {},
                orientation = Orientation.VERTICAL,
                itemContent = { item, _ -> Text(item) }
            )
        }

        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
    }
}
