package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.ui.artifacts.data.ArtifactFilterState

class FilterArtifactsUseCase {
    operator fun invoke(
        artifacts: List<Artifact>,
        searchQuery: String,
        filterState: ArtifactFilterState
    ): List<Artifact> {
        val searchedList = if (searchQuery.isBlank()) {
            artifacts
        } else {
            artifacts.filter { artifact ->
                artifact.setName.contains(searchQuery, ignoreCase = true) ||
                        artifact.artifactName.contains(searchQuery, ignoreCase = true)
            }
        }

        val setFilteredList = if(filterState.selectedArtifactSet == null){
            searchedList
        } else {
            searchedList.filter { artifact ->
                artifact.setName == filterState.selectedArtifactSet.name
            }
        }

        val levelFilteredList = setFilteredList.filter{ artifact ->
            artifact.level.toFloat() in filterState.selectedArtifactLevelRange
        }

        val slotFilteredList = if (filterState.selectedArtifactSlots.isEmpty()) {
            levelFilteredList
        } else {
            levelFilteredList.filter { artifact ->
                artifact.slot in filterState.selectedArtifactSlots
            }
        }

        val mainStatFilteredList = if (filterState.selectedArtifactMainStat == null) {
            slotFilteredList
        } else {
            slotFilteredList.filter { artifact ->
                artifact.mainStat.type == filterState.selectedArtifactMainStat
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