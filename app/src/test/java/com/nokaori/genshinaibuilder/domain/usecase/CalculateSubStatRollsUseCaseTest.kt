package com.nokaori.genshinaibuilder.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CalculateSubStatRollsUseCaseTest {
    private lateinit var useCase: CalculateSubStatRollsUseCase

    @Before
    fun setup() {
        useCase = CalculateSubStatRollsUseCase()
    }

    @Test
    fun invoke_withSingleRoll_returnsSingleValue() {
        val tierValues = listOf(0.05f, 0.10f, 0.15f)
        val result = useCase(0.10f, tierValues)
        assertEquals(listOf(0.10f), result)
    }

    @Test
    fun invoke_withMultipleRolls_returnsCombination() {
        val tierValues = listOf(0.05f, 0.10f)
        val result = useCase(0.15f, tierValues)
        assertEquals(2, result?.size)
    }

    @Test
    fun invoke_withZeroTarget_returnsNull() {
        val tierValues = listOf(0.05f, 0.10f)
        val result = useCase(0f, tierValues)
        assertNull(result)
    }

    @Test
    fun invoke_withEmptyTierValues_returnsNull() {
        val result = useCase(0.10f, emptyList())
        assertNull(result)
    }

    @Test
    fun invoke_withImpossibleTarget_returnsNull() {
        val tierValues = listOf(0.05f, 0.10f)
        val result = useCase(1.0f, tierValues)
        assertNull(result)
    }

    @Test
    fun invoke_withMaxSixRolls_findsCombination() {
        val tierValues = listOf(0.05f)
        val result = useCase(0.30f, tierValues)
        assertEquals(6, result?.size)
    }

    @Test
    fun invoke_withMoreThanSixRolls_returnsNull() {
        val tierValues = listOf(0.05f)
        val result = useCase(0.35f, tierValues)
        assertNull(result)
    }

    @Test
    fun invoke_withFloatingPointPrecision_findsApproximateMatch() {
        val tierValues = listOf(0.027f, 0.031f, 0.038f)
        val result = useCase(0.096f, tierValues)
        assertEquals(3, result?.size)
    }
}
