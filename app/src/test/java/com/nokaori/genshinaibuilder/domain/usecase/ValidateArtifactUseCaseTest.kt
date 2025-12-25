package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.data.EditorArtifactState
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.data.SubStatState
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateArtifactUseCaseTest {
    private lateinit var useCase: ValidateArtifactUseCase

    @Before
    fun setup() {
        useCase = ValidateArtifactUseCase()
    }

    @Test
    fun invoke_withValidArtifact_returnsSuccess() {
        val state = EditorArtifactState(
            selectedSet = ArtifactSet(id = 1, name = "Crimson Witch", iconUrl = ""),
            mainStatType = StatType.ATK_PERCENT,
            subStats = listOf(
                SubStatState(type = StatType.CRIT_RATE, rollHistory = listOf(0.05f)),
                SubStatState(type = StatType.CRIT_DMG, rollHistory = listOf(0.10f))
            ),
            level = 20,
            rarity = Rarity.FIVE_STARS
        )
        val result = useCase(state)
        assertTrue(result is ValidateArtifactUseCase.ValidationResult.Success)
    }

    @Test
    fun invoke_withoutSelectedSet_returnsError() {
        val state = EditorArtifactState(
            selectedSet = null,
            mainStatType = StatType.ATK_PERCENT,
            subStats = emptyList(),
            level = 20,
            rarity = Rarity.FIVE_STARS
        )
        val result = useCase(state)
        assertTrue(result is ValidateArtifactUseCase.ValidationResult.Error)
        assertTrue((result as ValidateArtifactUseCase.ValidationResult.Error).messages.any { it.contains("Set") })
    }

    @Test
    fun invoke_withoutMainStat_returnsError() {
        val state = EditorArtifactState(
            selectedSet = ArtifactSet(id = 1, name = "Crimson Witch", iconUrl = ""),
            mainStatType = null,
            subStats = emptyList(),
            level = 20,
            rarity = Rarity.FIVE_STARS
        )
        val result = useCase(state)
        assertTrue(result is ValidateArtifactUseCase.ValidationResult.Error)
        assertTrue((result as ValidateArtifactUseCase.ValidationResult.Error).messages.any { it.contains("Main Stat") })
    }

    @Test
    fun invoke_withZeroValueSubstat_returnsError() {
        val state = EditorArtifactState(
            selectedSet = ArtifactSet(id = 1, name = "Crimson Witch", iconUrl = ""),
            mainStatType = StatType.ATK_PERCENT,
            subStats = listOf(
                SubStatState(type = StatType.CRIT_RATE, rollHistory = emptyList())
            ),
            level = 20,
            rarity = Rarity.FIVE_STARS
        )
        val result = useCase(state)
        assertTrue(result is ValidateArtifactUseCase.ValidationResult.Error)
        assertTrue((result as ValidateArtifactUseCase.ValidationResult.Error).messages.any { it.contains("0 value") })
    }

    @Test
    fun invoke_withInvalidRollCount_returnsError() {
        val state = EditorArtifactState(
            selectedSet = ArtifactSet(id = 1, name = "Crimson Witch", iconUrl = ""),
            mainStatType = StatType.ATK_PERCENT,
            subStats = listOf(
                SubStatState(type = StatType.CRIT_RATE, rollHistory = listOf(0.05f)),
                SubStatState(type = StatType.CRIT_DMG, rollHistory = listOf(0.10f)),
                SubStatState(type = StatType.HP_PERCENT, rollHistory = listOf(0.15f)),
                SubStatState(type = StatType.DEF_PERCENT, rollHistory = listOf(0.20f)),
                SubStatState(type = StatType.ENERGY_RECHARGE, rollHistory = listOf(0.25f))
            ),
            level = 0,
            rarity = Rarity.FIVE_STARS
        )
        val result = useCase(state)
        assertTrue(result is ValidateArtifactUseCase.ValidationResult.Error)
    }

    @Test
    fun invoke_withDuplicateSubstats_returnsError() {
        val state = EditorArtifactState(
            selectedSet = ArtifactSet(id = 1, name = "Crimson Witch", iconUrl = ""),
            mainStatType = StatType.ATK_PERCENT,
            subStats = listOf(
                SubStatState(type = StatType.CRIT_RATE, rollHistory = listOf(0.05f)),
                SubStatState(type = StatType.CRIT_RATE, rollHistory = listOf(0.10f))
            ),
            level = 20,
            rarity = Rarity.FIVE_STARS
        )
        val result = useCase(state)
        assertTrue(result is ValidateArtifactUseCase.ValidationResult.Error)
        assertTrue((result as ValidateArtifactUseCase.ValidationResult.Error).messages.any { it.contains("Duplicate") })
    }

    @Test
    fun invoke_withMainStatAsSubstat_returnsError() {
        val state = EditorArtifactState(
            selectedSet = ArtifactSet(id = 1, name = "Crimson Witch", iconUrl = ""),
            mainStatType = StatType.ATK_PERCENT,
            subStats = listOf(
                SubStatState(type = StatType.ATK_PERCENT, rollHistory = listOf(0.05f))
            ),
            level = 20,
            rarity = Rarity.FIVE_STARS
        )
        val result = useCase(state)
        assertTrue(result is ValidateArtifactUseCase.ValidationResult.Error)
        assertTrue((result as ValidateArtifactUseCase.ValidationResult.Error).messages.any { it.contains("same as Main Stat") })
    }

    @Test
    fun invoke_withFourStarArtifact_acceptsValidRollCount() {
        val state = EditorArtifactState(
            selectedSet = ArtifactSet(id = 1, name = "Crimson Witch", iconUrl = ""),
            mainStatType = StatType.ATK_PERCENT,
            subStats = listOf(
                SubStatState(type = StatType.CRIT_RATE, rollHistory = listOf(0.05f)),
                SubStatState(type = StatType.CRIT_DMG, rollHistory = listOf(0.10f))
            ),
            level = 20,
            rarity = Rarity.FOUR_STARS
        )
        val result = useCase(state)
        assertTrue(result is ValidateArtifactUseCase.ValidationResult.Success)
    }

    @Test
    fun invoke_withThreeStarArtifact_acceptsValidRollCount() {
        val state = EditorArtifactState(
            selectedSet = ArtifactSet(id = 1, name = "Crimson Witch", iconUrl = ""),
            mainStatType = StatType.ATK_PERCENT,
            subStats = listOf(
                SubStatState(type = StatType.CRIT_RATE, rollHistory = listOf(0.05f))
            ),
            level = 4,
            rarity = Rarity.THREE_STARS
        )
        val result = useCase(state)
        assertTrue(result is ValidateArtifactUseCase.ValidationResult.Success)
    }
}
