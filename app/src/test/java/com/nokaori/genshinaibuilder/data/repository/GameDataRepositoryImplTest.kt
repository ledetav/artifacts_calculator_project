package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.ArtifactDao
import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.local.dao.StatCurveDao
import com.nokaori.genshinaibuilder.data.local.dao.WeaponDao
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GameDataRepositoryImplTest {
    @Mock
    private lateinit var characterDao: CharacterDao
    @Mock
    private lateinit var statCurveDao: StatCurveDao
    @Mock
    private lateinit var weaponDao: WeaponDao
    @Mock
    private lateinit var artifactDao: ArtifactDao
    @Mock
    private lateinit var api: YattaApi

    private val stubSettingsRepository = object : SettingsRepository {
        override val appLanguage: Flow<String> = flowOf("en")
        override val lastSyncTime: Flow<Long> = flowOf(0L)
        override suspend fun setAppLanguage(language: String) {}
        override suspend fun setLastSyncTime(time: Long) {}
    }

    private lateinit var repository: GameDataRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = GameDataRepositoryImpl(
            characterDao = characterDao,
            statCurveDao = statCurveDao,
            weaponDao = weaponDao,
            artifactDao = artifactDao,
            api = api,
            themeRepository = stubSettingsRepository
        )
    }

    @Test
    fun updateGameData_emitsInProgressStatus() = runTest {
        whenever(api.getAvatarCurves()).thenThrow(RuntimeException("Network error"))

        val statuses = mutableListOf<SyncStatus>()
        repository.updateGameData().collect { statuses.add(it) }

        assertTrue(statuses.any { it is SyncStatus.Error })
    }

    @Test
    fun updateGameData_withNetworkError_emitsErrorStatus() = runTest {
        whenever(api.getAvatarCurves()).thenThrow(RuntimeException("Network error"))

        val statuses = mutableListOf<SyncStatus>()
        repository.updateGameData().collect { statuses.add(it) }

        val lastStatus = statuses.lastOrNull()
        assertTrue(lastStatus is SyncStatus.Error)
    }
}
