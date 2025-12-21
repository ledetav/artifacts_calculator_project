package com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.data

import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.StatType

data class EditorArtifactState(
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
    val availableMainStats: List<StatType> = emptyList()
)