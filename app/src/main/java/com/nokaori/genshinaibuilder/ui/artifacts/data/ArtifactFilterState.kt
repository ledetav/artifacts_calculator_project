package com.nokaori.genshinaibuilder.ui.artifacts.data

import com.nokaori.genshinaibuilder.data.ArtifactSet
import com.nokaori.genshinaibuilder.data.ArtifactSlot
import com.nokaori.genshinaibuilder.data.StatType

data class ArtifactFilterState(
    val selectedArtifactSet: ArtifactSet? = null,
    val artifactSetSearchQuery: String = "",
    val isArtifactSetDropdownExpanded: Boolean = false,
    val selectedArtifactLevelRange: ClosedFloatingPointRange<Float> = 0f..20f,
    val selectedArtifactSlots: Set<ArtifactSlot> = emptySet(),
    val selectedArtifactMainStat: StatType? = null
)