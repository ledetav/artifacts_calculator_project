package com.nokaori.genshinaibuilder.domain.usecase

import javax.inject.Inject
import kotlin.math.abs

class CalculateSubStatRollsUseCase @Inject constructor() {

    /**
     * Пытается найти комбинацию роллов (от 1 до 6), сумма которых равна targetValue.
     * @param targetValue Целевое значение (например, 0.14 для 14%)
     * @param tierValues Возможные значения одного ролла (например, [0.027, 0.031, ...])
     * @return Список роллов или null, если комбинация не найдена.
     */
    operator fun invoke(targetValue: Float, tierValues: List<Float>): List<Float>? {
        if (tierValues.isEmpty() || targetValue <= 0.0001f) return null

        for (count in 1..6) {
            val result = findCombination(targetValue, tierValues, count)
            if (result != null) return result
        }
        return null
    }

    private fun findCombination(target: Float, tiers: List<Float>, depth: Int): List<Float>? {
        if (depth == 0) {
            return if (abs(target) < 0.0005f) emptyList() else null
        }

        val sortedTiers = tiers.sortedDescending()

        for (tier in sortedTiers) {
            val remainder = target - tier

            if (remainder < -0.0005f) continue

            val subResult = findCombination(remainder, tiers, depth - 1)
            if (subResult != null) {
                return listOf(tier) + subResult
            }
        }
        return null
    }
}