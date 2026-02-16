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
            errors.add("Please select an Artifact Set.")
        }

        if (state.mainStatType == null) {
            errors.add("Please select a Main Stat.")
        }

        if (state.subStats.any { it.type == null }) {
            errors.add("Some substats are empty. Please select a stat type or remove them.")
        }

        val filledSubStats = state.subStats.filter { it.type != null }
        
        if (filledSubStats.any { it.value <= 0f }) {
            errors.add("Some substats have a value of 0. Please enter a value or remove them.")
        }

        val totalRolls = filledSubStats.sumOf { it.rollCount }
        val level = state.level
        val rarity = state.rarity

        val (minInitial, maxInitial) = getInitialLinesRange(rarity)
        val upgrades = level / 4

        val minPossible = minInitial + upgrades
        val maxPossible = maxInitial + upgrades

        if (totalRolls < minPossible || totalRolls > maxPossible) {
            val expectedText = if (minPossible == maxPossible) "$minPossible" else "$minPossible to $maxPossible"
            errors.add("A Lv.$level ${rarity.stars}★ artifact should have $expectedText total substat rolls, but currently has $totalRolls.")
        }

        val minLines = minOf(4, minInitial + upgrades)
        val maxLines = minOf(4, maxInitial + upgrades)
        val currentLines = filledSubStats.size

        if (currentLines < minLines || currentLines > maxLines) {
            val expectedText = if (minLines == maxLines) "$minLines" else "$minLines to $maxLines"
            errors.add("A Lv.$level ${rarity.stars}★ artifact should have $expectedText substat lines, but currently has $currentLines.")
        }

        val types = filledSubStats.mapNotNull { it.type }
        
        if (types.size != types.toSet().size) {
            errors.add("Duplicate substats found. Each substat must be unique.")
        }

        if (state.mainStatType != null && state.mainStatType in types) {
            errors.add("A substat cannot be the same as the Main Stat.")
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