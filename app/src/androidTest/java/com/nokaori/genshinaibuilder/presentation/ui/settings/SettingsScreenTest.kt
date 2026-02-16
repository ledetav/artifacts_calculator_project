package com.nokaori.genshinaibuilder.presentation.ui.settings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSettingsScreenTitleDisplayed() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            SettingsScreen(settingsViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Настройки данных").assertIsDisplayed()
    }

    @Test
    fun testUpdateButtonDisplayed() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            SettingsScreen(settingsViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Обновить базу персонажей").assertIsDisplayed()
    }

    @Test
    fun testUpdateButtonClickCallsViewModel() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            SettingsScreen(settingsViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Обновить базу персонажей").performClick()
        verify(mockViewModel).updateDatabase()
    }

    @Test
    fun testIdleStatusDisplaysWaitingMessage() {
        val mockViewModel = createMockViewModel(syncStatus = SyncStatus.Idle)
        composeTestRule.setContent {
            SettingsScreen(settingsViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Ожидание...").assertIsDisplayed()
    }

    @Test
    fun testInProgressStatusDisplaysProgressBar() {
        val inProgressStatus = SyncStatus.InProgress(
            progress = 0.5f,
            message = "Загрузка данных...",
            logs = listOf("Log 1", "Log 2")
        )
        val mockViewModel = createMockViewModel(syncStatus = inProgressStatus)
        composeTestRule.setContent {
            SettingsScreen(settingsViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Загрузка данных...").assertIsDisplayed()
    }

    @Test
    fun testSuccessStatusDisplaysMessage() {
        val successStatus = SyncStatus.Success(
            summary = "Успешно обновлено",
            fullLogs = listOf("Log 1")
        )
        val mockViewModel = createMockViewModel(syncStatus = successStatus)
        composeTestRule.setContent {
            SettingsScreen(settingsViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Успешно обновлено").assertIsDisplayed()
    }

    @Test
    fun testErrorStatusDisplaysErrorMessage() {
        val errorStatus = SyncStatus.Error("Ошибка подключения")
        val mockViewModel = createMockViewModel(syncStatus = errorStatus)
        composeTestRule.setContent {
            SettingsScreen(settingsViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Ошибка: Ошибка подключения").assertIsDisplayed()
    }

    @Test
    fun testUpdateButtonDisabledDuringSync() {
        val inProgressStatus = SyncStatus.InProgress(
            progress = 0.5f,
            message = "Загрузка...",
            logs = emptyList()
        )
        val mockViewModel = createMockViewModel(syncStatus = inProgressStatus)
        composeTestRule.setContent {
            SettingsScreen(settingsViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Обновить базу персонажей").assertIsNotEnabled()
    }

    private fun createMockViewModel(
        syncStatus: SyncStatus = SyncStatus.Idle
    ): SettingsViewModel {
        return mock {
            on { this.syncStatus }.thenReturn(MutableStateFlow(syncStatus))
        }
    }
}
