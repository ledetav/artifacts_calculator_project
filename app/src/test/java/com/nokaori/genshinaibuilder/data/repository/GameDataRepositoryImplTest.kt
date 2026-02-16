package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.local.dao.ArtifactDao
import com.nokaori.genshinaibuilder.data.local.dao.CharacterDao
import com.nokaori.genshinaibuilder.data.local.dao.StatCurveDao
import com.nokaori.genshinaibuilder.data.local.dao.WeaponDao
import com.nokaori.genshinaibuilder.data.remote.api.YattaApi
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
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

    private lateinit var repository: GameDataRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = GameDataRepositoryImpl(characterDao, statCurveDao, weaponDao, artifactDao, api)
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
