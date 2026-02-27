package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.UiText
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.data.EditorArtifactState
import javax.inject.Inject

class ValidateArtifactUseCase @Inject constructor() {

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val messages: List<UiText>) : ValidationResult()
    }

    operator fun invoke(state: EditorArtifactState): ValidationResult {
        val errors = mutableListOf<UiText>()

        if (state.selectedSet == null) {
            errors.add(UiText.StringResource(R.string.artifact_error_no_set))
        }

        if (state.mainStatType == null) {
            errors.add(UiText.StringResource(R.string.artifact_error_no_main_stat))
        }

        if (state.subStats.any { it.type == null }) {
            errors.add(UiText.StringResource(R.string.artifact_error_empty_substats))
        }

        val filledSubStats = state.subStats.filter { it.type != null }
        
        if (filledSubStats.any { it.value <= 0f }) {
            errors.add(UiText.StringResource(R.string.artifact_error_zero_substats))
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
            errors.add(UiText.StringResource(R.string.artifact_error_total_rolls, level, rarity.stars, expectedText, totalRolls))
        } else {
            val expectedLines = minOf(4, totalRolls)
            val currentLines = filledSubStats.size

            if (currentLines != expectedLines) {
                errors.add(UiText.StringResource(R.string.artifact_error_line_count, expectedLines, currentLines))
            }
        }

        val types = filledSubStats.mapNotNull { it.type }
        
        if (types.size != types.toSet().size) {
            errors.add(UiText.StringResource(R.string.artifact_error_duplicate_substats))
        }

        if (state.mainStatType != null && state.mainStatType in types) {
            errors.add(UiText.StringResource(R.string.artifact_error_main_sub_conflict))
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