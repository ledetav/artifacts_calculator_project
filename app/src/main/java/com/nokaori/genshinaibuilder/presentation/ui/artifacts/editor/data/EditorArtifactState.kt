package com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.data

import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.StatType

data class EditorArtifactState(
    val artifactId: Int? = null,
    val selectedSet: ArtifactSet? = null,
    val slot: ArtifactSlot = ArtifactSlot.FLOWER_OF_LIFE,
    val rarity: Rarity = Rarity.FIVE_STARS,

    val availableRarities: List<Rarity> = listOf(
        Rarity.THREE_STARS,
        Rarity.FOUR_STARS,
        Rarity.FIVE_STARS
    ),
    val currentPieceIconUrl: String? = null,

    val isSetSelectionDialogOpen: Boolean = false,
    val setSearchQuery: String = "",

    val level: Int = 0,
    val maxLevel: Int = 20,
    val mainStatType: StatType? = null,
    val mainStatValue: Float = 0f,
    val availableMainStats: List<StatType> = emptyList(),

    val subStats: List<SubStatState> = emptyList(),
    val maxSubStatsCount: Int = 4,
    val maxRollsPerLine: Int = 6,
    val currentMaxTotalRolls: Int = 9,

    val validationErrors: List<String> = emptyList(),
    val isSaveSuccess: Boolean = false
)

data class SubStatState(
    val id: Long = System.currentTimeMillis(),
    val type: StatType? = null,
    val rollHistory: List<Float> = emptyList(),
    val tierValues: List<Float> = emptyList(),
    val availableTypes: List<StatType> = emptyList()
) {
    val value: Float get() = rollHistory.sum()
    val rollCount: Int get() = rollHistory.size
}