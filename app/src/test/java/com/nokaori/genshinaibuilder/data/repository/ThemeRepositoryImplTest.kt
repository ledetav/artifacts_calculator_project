package com.nokaori.genshinaibuilder.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class ThemeRepositoryImplTest {
    @Mock
    private lateinit var dataStore: DataStore<Preferences>

    private lateinit var repository: ThemeRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun isDarkTheme_withDarkModeEnabled_returnsTrue() = runTest {
        val darkModeKey = booleanPreferencesKey("is_darkmode")
        val preferences = preferencesOf(darkModeKey to true)

        whenever(dataStore.data).thenReturn(flowOf(preferences))

        val result = mutableListOf<Boolean>()
        repository.isDarkTheme.collect { result.add(it) }

        assertTrue(result[0])
    }

    @Test
    fun isDarkTheme_withDarkModeDisabled_returnsFalse() = runTest {
        val darkModeKey = booleanPreferencesKey("is_darkmode")
        val preferences = preferencesOf(darkModeKey to false)

        whenever(dataStore.data).thenReturn(flowOf(preferences))

        val result = mutableListOf<Boolean>()
        repository.isDarkTheme.collect { result.add(it) }

        assertFalse(result[0])
    }

    @Test
    fun isDarkTheme_withNoPreference_returnsFalse() = runTest {
        val preferences = preferencesOf()

        whenever(dataStore.data).thenReturn(flowOf(preferences))

        val result = mutableListOf<Boolean>()
        repository.isDarkTheme.collect { result.add(it) }

        assertFalse(result[0])
    }
}
