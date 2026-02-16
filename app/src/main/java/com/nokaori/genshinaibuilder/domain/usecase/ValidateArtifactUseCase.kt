package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.data.EditorArtifactState
import javax.inject.Inject

class ValidateArtifactUseCase @Inject constructor() {

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val messages: List<String>) : ValidationResult()
    }

    operator fun invoke(state: EditorArtifactState): ValidationResult {
        val errors = mutableListOf<String>()

        if (state.selectedSet == null) {
            errors.add("Artifact Set is not selected.")
        }

        if (state.mainStatType == null) {
            errors.add("Main Stat is not selected.")
        }

        val filledSubStats = state.subStats.filter { it.type != null }
        if (filledSubStats.any { it.value <= 0f }) {
            errors.add("Some substats have 0 value.")
        }

        val totalRolls = filledSubStats.sumOf { it.rollCount }
        val level = state.level
        val rarity = state.rarity

        val (minInitial, maxInitial) = getInitialLinesRange(rarity)
        val upgrades = level / 4

        val minTotal = minInitial + (if (level >= 4) 0 else 0)

        val minPossible = minInitial + upgrades
        val maxPossible = maxInitial + upgrades

        if (totalRolls < minPossible || totalRolls > maxPossible) {
            errors.add("Invalid number of rolls for Lv.$level ${rarity.stars}★ artifact.\n" +
                    "Current: $totalRolls rolls.\n" +
                    "Expected: $minPossible..$maxPossible rolls.")
        }

        val types = filledSubStats.mapNotNull { it.type }
        if (types.size != types.toSet().size) {
            errors.add("Duplicate substats found.")
        }

        if (state.mainStatType in types) {
            errors.add("Substat cannot be the same as Main Stat.")
        }

        return if (errors.isEmpty()) ValidationResult.Success else ValidationResult.Error(errors)
    }

    private fun getInitialLinesRange(rarity: Rarity): Pair<Int, Int> {
        return when (rarity) {
            Rarity.FIVE_STARS -> 3 to 4
            Rarity.FOUR_STARS -> 2 to 3
            Rarity.THREE_STARS -> 1 to 2
            else -> 0 to 0
        }
    }
}