package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.StatCurve
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateArtifactMainStatUseCaseTest {
    private lateinit var useCase: CalculateArtifactMainStatUseCase

    @Before
    fun setup() {
        useCase = CalculateArtifactMainStatUseCase()
    }

    @Test
    fun invoke_withValidCurveAndLevel_returnsCorrectValue() {
        val curve = StatCurve(
            id = "test",
            points = mapOf(0 to 100f, 1 to 150f, 20 to 500f)
        )
        val result = useCase(20, curve)
        assertEquals(500f, result)
    }

    @Test
    fun invoke_withNullCurve_returnsZero() {
        val result = useCase(20, null)
        assertEquals(0f, result)
    }

    @Test
    fun invoke_withLevelNotInCurve_returnsZero() {
        val curve = StatCurve(
            id = "test",
            points = mapOf(0 to 100f, 1 to 150f)
        )
        val result = useCase(99, curve)
        assertEquals(0f, result)
    }

    @Test
    fun invoke_withLevelZero_returnsFirstValue() {
        val curve = StatCurve(
            id = "test",
            points = mapOf(0 to 100f, 1 to 150f)
        )
        val result = useCase(0, curve)
        assertEquals(100f, result)
    }
}
