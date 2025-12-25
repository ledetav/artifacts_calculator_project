package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateGameDataUseCaseTest {
    private lateinit var useCase: UpdateGameDataUseCase
    private lateinit var mockRepository: GameDataRepository

    @Before
    fun setup() {
        mockRepository = object : GameDataRepository {
            override fun updateGameData() = flowOf(
                SyncStatus.InProgress("Fetching data", 0.5f, listOf("Started")),
                SyncStatus.Success("Update complete", listOf("Success"))
            )
        }
        useCase = UpdateGameDataUseCase(mockRepository)
    }

    @Test
    fun invoke_returnsFlowFromRepository() = runBlocking {
        val flow = useCase()
        val results = mutableListOf<SyncStatus>()
        flow.collect { results.add(it) }
        assertEquals(2, results.size)
    }

    @Test
    fun invoke_emitsInProgressStatus() = runBlocking {
        val flow = useCase()
        val results = mutableListOf<SyncStatus>()
        flow.collect { results.add(it) }
        val inProgress = results[0] as SyncStatus.InProgress
        assertEquals(0.5f, inProgress.progress)
    }

    @Test
    fun invoke_emitsSuccessStatus() = runBlocking {
        val flow = useCase()
        val results = mutableListOf<SyncStatus>()
        flow.collect { results.add(it) }
        val success = results[1] as SyncStatus.Success
        assertEquals("Update complete", success.summary)
    }

    @Test
    fun invoke_withErrorStatus_emitsError() = runBlocking {
        val errorRepository = object : GameDataRepository {
            override fun updateGameData() = flowOf(
                SyncStatus.Error("Network error")
            )
        }
        val errorUseCase = UpdateGameDataUseCase(errorRepository)
        val flow = errorUseCase()
        val results = mutableListOf<SyncStatus>()
        flow.collect { results.add(it) }
        val error = results[0] as SyncStatus.Error
        assertEquals("Network error", error.message)
    }
}
