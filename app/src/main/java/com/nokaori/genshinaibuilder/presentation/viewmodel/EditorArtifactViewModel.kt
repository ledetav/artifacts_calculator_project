package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.*
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.usecase.CalculateArtifactMainStatUseCase
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.data.EditorArtifactState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorArtifactViewModel @Inject constructor(
    private val artifactRepository: ArtifactRepository,
    private val calculateMainStatUseCase: CalculateArtifactMainStatUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditorArtifactState())
    val state: StateFlow<EditorArtifactState> = _state.asStateFlow()

    private val _allSets = artifactRepository.getAvailableArtifactSets()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredSets: StateFlow<List<ArtifactSet>> = combine(_allSets, _state) { sets, state ->
        if (state.setSearchQuery.isBlank()) sets
        else sets.filter { it.name.contains(state.setSearchQuery, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var currentMainStatCurve: StatCurve? = null

    init {
        updateMainStatsForCurrentSlot()
    }

    fun onSetClicked() {
        _state.update { it.copy(isSetSelectionDialogOpen = true) }
    }

    private fun getIconUrlForCurrentSelection(set: ArtifactSet?, slot: ArtifactSlot): String? {
        if (set == null) return null
        return set.pieces.find { it.slot == slot }?.iconUrl ?: set.iconUrl
    }

    fun onSetSelected(shortSetInfo: ArtifactSet) {
        viewModelScope.launch {
            val fullSet = try {
                artifactRepository.getArtifactSetDetails(shortSetInfo.id)
            } catch (e: Exception) {
                shortSetInfo
            }

            val newRarities = fullSet.rarities.sortedBy { it.stars }
            val currentRarity = _state.value.rarity
            val newSelectedRarity = if (currentRarity in newRarities) currentRarity else newRarities.lastOrNull() ?: Rarity.FIVE_STARS

            val newIconUrl = getIconUrlForCurrentSelection(fullSet, _state.value.slot)

            _state.update {
                it.copy(
                    selectedSet = fullSet,
                    availableRarities = newRarities,
                    rarity = newSelectedRarity,
                    currentPieceIconUrl = newIconUrl,
                    isSetSelectionDialogOpen = false,
                    setSearchQuery = ""
                )
            }
            // Пересчитываем макс. уровень при смене сета (т.к. могла смениться редкость)
            onRarityChanged(newSelectedRarity)
        }
    }

    fun onSlotChanged(slot: ArtifactSlot) {
        val newIconUrl = getIconUrlForCurrentSelection(_state.value.selectedSet, slot)

        _state.update {
            it.copy(
                slot = slot,
                currentPieceIconUrl = newIconUrl
            )
        }

        updateMainStatsForCurrentSlot()
    }

    fun onRarityChanged(rarity: Rarity) {
        val newMaxLevel = when(rarity) {
            Rarity.FIVE_STARS -> 20
            Rarity.FOUR_STARS -> 16
            Rarity.THREE_STARS -> 12
            else -> 4
        }

        val newLevel = if (_state.value.level > newMaxLevel) newMaxLevel else _state.value.level

        _state.update {
            it.copy(
                rarity = rarity,
                maxLevel = newMaxLevel,
                level = newLevel
            )
        }

        updateMainStatCurve()
    }

    fun onMainStatTypeChanged(type: StatType) {
        _state.update { it.copy(mainStatType = type) }
        updateMainStatCurve()
    }

    fun onLevelChanged(level: Int) {
        _state.update { it.copy(level = level) }
        recalculateValue()
    }

    private fun updateMainStatsForCurrentSlot() {
        val slot = _state.value.slot
        val allowedStats = ArtifactRules.getAllowedMainStats(slot)

        val currentStat = _state.value.mainStatType
        val newStat = if (currentStat in allowedStats) currentStat else allowedStats.first()

        _state.update {
            it.copy(
                availableMainStats = allowedStats,
                mainStatType = newStat
            )
        }

        updateMainStatCurve()
    }

    private fun updateMainStatCurve() {
        val rarity = _state.value.rarity.stars
        val type = _state.value.mainStatType ?: return

        viewModelScope.launch {
            currentMainStatCurve = artifactRepository.getArtifactMainStatCurve(rarity, type)
            recalculateValue()
        }
    }

    private fun recalculateValue() {
        val value = calculateMainStatUseCase(_state.value.level, currentMainStatCurve)
        _state.update { it.copy(mainStatValue = value) }
    }

    fun onSetDialogDismiss() {
        _state.update { it.copy(isSetSelectionDialogOpen = false, setSearchQuery = "") }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(setSearchQuery = query) }
    }
}