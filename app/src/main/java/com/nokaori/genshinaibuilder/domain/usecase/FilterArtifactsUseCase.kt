package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType
import javax.inject.Inject

class FilterArtifactsUseCase @Inject constructor() {
    operator fun invoke(
        artifacts: List<Artifact>,
        searchQuery: String,
        selectedSet: ArtifactSet?,
        levelRange: ClosedFloatingPointRange<Float>,
        selectedSlots: Set<ArtifactSlot>,
        selectedMainStat: StatType?
    ): List<Artifact> {
        val searchedList = if (searchQuery.isBlank()) {
            artifacts
        } else {
            artifacts.filter { artifact ->
                artifact.setName.contains(searchQuery, ignoreCase = true) ||
                        artifact.artifactName.contains(searchQuery, ignoreCase = true)
            }
        }

        val setFilteredList = if(selectedSet == null){
            searchedList
        } else {
            searchedList.filter { artifact ->
                artifact.setName == selectedSet.name
            }
        }

        val levelFilteredList = setFilteredList.filter{ artifact ->
            artifact.level.toFloat() in levelRange
        }

        val slotFilteredList = if (selectedSlots.isEmpty()) {
            levelFilteredList
        } else {
            levelFilteredList.filter { artifact ->
                artifact.slot in selectedSlots
            }
        }

        val mainStatFilteredList = if (selectedMainStat == null) {
            slotFilteredList
        } else {
            slotFilteredList.filter { artifact ->
                artifact.mainStat.type == selectedMainStat
            }
        }

        return mainStatFilteredList.sortedBy { artifact ->
            when {
                searchQuery.isNotBlank() &&
                        artifact.artifactName.contains(searchQuery, ignoreCase = true) -> 0
                else -> 1
            }
        }
    }
}