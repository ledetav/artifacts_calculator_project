package com.nokaori.genshinaibuilder.presentation.ui.weapons

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import com.nokaori.genshinaibuilder.presentation.ui.weapons.data.WeaponFilterState
import com.nokaori.genshinaibuilder.presentation.viewmodel.WeaponViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class WeaponScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSearchFieldDisplayed() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            WeaponScreen(weaponViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Поиск оружия").assertIsDisplayed()
    }

    @Test
    fun testSearchQueryUpdatesOnInput() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            WeaponScreen(weaponViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Поиск оружия").performTextInput("sword")
        verify(mockViewModel).onSearchQueryChange("sword")
    }

    @Test
    fun testFilterIconClickOpensDialog() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            WeaponScreen(weaponViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithContentDescription("Фильтр").performClick()
        verify(mockViewModel).onFilterIconClicked()
    }

    @Test
    fun testWeaponListDisplaysItems() {
        val weapons = listOf(
            createTestUserWeapon("Sword 1"),
            createTestUserWeapon("Sword 2")
        )
        val mockViewModel = createMockViewModel(weapons = weapons)
        composeTestRule.setContent {
            WeaponScreen(weaponViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Sword 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sword 2").assertIsDisplayed()
    }

    @Test
    fun testEmptyWeaponListDisplaysNoItems() {
        val mockViewModel = createMockViewModel(weapons = emptyList())
        composeTestRule.setContent {
            WeaponScreen(weaponViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Sword 1").assertDoesNotExist()
    }

    private fun createMockViewModel(
        searchQuery: String = "",
        weapons: List<UserWeapon> = emptyList(),
        isFilterDialogShown: Boolean = false
    ): WeaponViewModel {
        return mock {
            on { this.searchQuery }.thenReturn(MutableStateFlow(searchQuery))
            on { searchedWeapons }.thenReturn(MutableStateFlow(weapons))
            on { this.isFilterDialogShown }.thenReturn(MutableStateFlow(isFilterDialogShown))
            on { draftWeaponFilterState }.thenReturn(MutableStateFlow(WeaponFilterState()))
            on { areWeaponFiltersChanged }.thenReturn(MutableStateFlow(false))
        }
    }

    private fun createTestUserWeapon(name: String): UserWeapon {
        return UserWeapon(
            id = 1,
            weapon = Weapon(
                id = 1,
                name = name,
                type = WeaponType.SWORD,
                rarity = 5,
                mainStat = StatType.ATK,
                mainStatValue = 100f
            ),
            level = 90,
            ascension = 6
        )
    }
}
