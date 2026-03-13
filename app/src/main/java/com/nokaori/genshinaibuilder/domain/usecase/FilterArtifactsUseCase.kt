package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

class FilterArtifactsUseCase @Inject constructor() {
    private val collator = Collator.getInstance(Locale("ru")).apply {
        strength = Collator.SECONDARY
    }
    operator fun invoke(
        artifacts: List<Artifact>,
        searchQuery: String,
        selectedSet: ArtifactSet?,
        levelRange: ClosedFloatingPointRange<Float>,
        selectedSlots: Set<ArtifactSlot>,
        selectedMainStat: StatType?
    ): List<Artifact> {
        var filtered = artifacts

        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter { artifact ->
                artifact.setName.contains(searchQuery, ignoreCase = true) ||
                        artifact.artifactName.contains(searchQuery, ignoreCase = true)
            }
        }

        if (selectedSet != null) {
            filtered = filtered.filter { artifact ->
                artifact.setName == selectedSet.name
            }
        }

        filtered = filtered.filter { artifact ->
            artifact.level.toFloat() in levelRange
        }

        if (selectedSlots.isNotEmpty()) {
            filtered = filtered.filter { artifact ->
                artifact.slot in selectedSlots
            }
        }

        if (selectedMainStat != null) {
            filtered = filtered.filter { artifact ->
                artifact.mainStat.type == selectedMainStat
            }
        }

        return filtered.sortedWith(
            compareBy<Artifact> { artifact ->
                when {
                    searchQuery.isNotBlank() &&
                            artifact.artifactName.equals(searchQuery, ignoreCase = true) -> 0
                    searchQuery.isNotBlank() &&
                            artifact.artifactName.contains(searchQuery, ignoreCase = true) -> 1
                    else -> 2
                }
            }.thenBy(collator) { it.artifactName }
        )
    }
}