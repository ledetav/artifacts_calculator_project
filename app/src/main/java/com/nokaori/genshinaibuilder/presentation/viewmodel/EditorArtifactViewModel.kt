package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.*
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.usecase.CalculateArtifactMainStatUseCase
import com.nokaori.genshinaibuilder.domain.usecase.CalculateSubStatRollsUseCase
import com.nokaori.genshinaibuilder.domain.usecase.ValidateArtifactUseCase
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.data.EditorArtifactState
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.data.SubStatState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorArtifactViewModel @Inject constructor(
    private val artifactRepository: ArtifactRepository,
    private val calculateMainStatUseCase: CalculateArtifactMainStatUseCase,
    private val calculateSubStatRollsUseCase: CalculateSubStatRollsUseCase,
    private val validateArtifactUseCase: ValidateArtifactUseCase
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

    fun onSetClicked() { _state.update { it.copy(isSetSelectionDialogOpen = true) } }
    fun onSetDialogDismiss() { _state.update { it.copy(isSetSelectionDialogOpen = false, setSearchQuery = "") } }
    fun onSearchQueryChange(query: String) { _state.update { it.copy(setSearchQuery = query) } }

    fun onSetSelected(shortSetInfo: ArtifactSet) {
        viewModelScope.launch {
            val fullSet = try { artifactRepository.getArtifactSetDetails(shortSetInfo.id) } catch (e: Exception) { shortSetInfo }
            val newRarities = fullSet.rarities.sortedBy { it.stars }
            val currentRarity = _state.value.rarity
            val newSelectedRarity = if (currentRarity in newRarities) currentRarity else newRarities.lastOrNull() ?: Rarity.FIVE_STARS
            val newIconUrl = getIconUrlForCurrentSelection(fullSet, _state.value.slot)

            _state.update {
                it.copy(selectedSet = fullSet, availableRarities = newRarities, rarity = newSelectedRarity, currentPieceIconUrl = newIconUrl, isSetSelectionDialogOpen = false, setSearchQuery = "")
            }
            onRarityChanged(newSelectedRarity)
        }
    }

    fun onRarityChanged(rarity: Rarity) {
        val newMaxLevel = when(rarity) { Rarity.FIVE_STARS -> 20; Rarity.FOUR_STARS -> 16; Rarity.THREE_STARS -> 12; else -> 4 }
        val newLevel = if (_state.value.level > newMaxLevel) newMaxLevel else _state.value.level
        _state.update { it.copy(rarity = rarity, maxLevel = newMaxLevel, level = newLevel) }
        updateMainStatsForCurrentSlot()
        refreshAllSubStatValues()
    }

    fun onSlotChanged(slot: ArtifactSlot) {
        val newIconUrl = getIconUrlForCurrentSelection(_state.value.selectedSet, slot)
        _state.update { it.copy(slot = slot, currentPieceIconUrl = newIconUrl) }
        updateMainStatsForCurrentSlot()
    }

    private fun getIconUrlForCurrentSelection(set: ArtifactSet?, slot: ArtifactSlot): String? {
        if (set == null) return null
        return set.pieces.find { it.slot == slot }?.iconUrl ?: set.iconUrl
    }

    fun onMainStatTypeChanged(type: StatType) {
        _state.update { it.copy(mainStatType = type) }
        updateMainStatCurve()
        checkMainStatConflict()
        updateAllSubStatsAvailableTypes()
    }

    fun onLevelChanged(level: Int) {
        _state.update { it.copy(level = level) }
        recalculateMainStatValue()
    }

    fun onAddSubStat() {
        if (_state.value.subStats.size >= _state.value.maxSubStatsCount) return
        val newSubStat = SubStatState(id = System.nanoTime(), availableTypes = getAvailableSubStatsTypes(excludeId = -1))
        _state.update { it.copy(subStats = it.subStats + newSubStat) }
    }

    fun onRemoveSubStat(id: Long) {
        _state.update { state -> state.copy(subStats = state.subStats.filter { it.id != id }) }
        updateAllSubStatsAvailableTypes()
    }

    fun onSubStatTypeChanged(id: Long, type: StatType) {
        viewModelScope.launch {
            val rarity = _state.value.rarity.stars
            val tierValues = artifactRepository.getArtifactSubStatRolls(rarity, type) ?: emptyList()

            _state.update { state ->
                val newList = state.subStats.map { subStat ->
                    if (subStat.id == id) {
                        val baseVal = tierValues.lastOrNull() ?: 0f
                        subStat.copy(type = type, tierValues = tierValues, rollHistory = listOf(baseVal))
                    } else subStat
                }
                state.copy(subStats = newList)
            }
            updateAllSubStatsAvailableTypes()
        }
    }

    fun onSubStatRollAdded(id: Long, addedValue: Float) {
        _state.update { state ->
            val newList = state.subStats.map { subStat ->
                if (subStat.id == id && subStat.rollCount < 6) {
                    subStat.copy(rollHistory = subStat.rollHistory + addedValue)
                } else subStat
            }
            state.copy(subStats = newList)
        }
    }

    fun onSubStatRollRemoved(id: Long, indexToRemove: Int) {
        _state.update { state ->
            val newList = state.subStats.map { subStat ->
                if (subStat.id == id) {
                    val newHistory = subStat.rollHistory.toMutableList()
                    if (indexToRemove in newHistory.indices) newHistory.removeAt(indexToRemove)
                    subStat.copy(rollHistory = newHistory)
                } else subStat
            }
            state.copy(subStats = newList)
        }
    }

    fun onSubStatManualValueEntered(id: Long, valueString: String) {
        val value = valueString.toFloatOrNull() ?: return

        _state.update { state ->
            val newList = state.subStats.map { subStat ->
                if (subStat.id == id) {
                    val isPercent = subStat.type?.isPercentage == true

                    val isDbFraction = (subStat.tierValues.firstOrNull() ?: 0f) < 1f
                    val normalizedValue = if (isPercent && isDbFraction && value > 1f) value / 100f else value

                    val rolls = calculateSubStatRollsUseCase(normalizedValue, subStat.tierValues)

                    if (rolls != null) {
                        subStat.copy(rollHistory = rolls)
                    } else {
                        subStat.copy(rollHistory = listOf(normalizedValue))
                    }
                } else subStat
            }
            state.copy(subStats = newList)
        }
    }

    private fun updateMainStatsForCurrentSlot() {
        val slot = _state.value.slot
        val allowedStats = ArtifactRules.getAllowedMainStats(slot)
        val currentStat = _state.value.mainStatType
        val newStat = if (currentStat in allowedStats) currentStat else allowedStats.first()
        _state.update { it.copy(availableMainStats = allowedStats, mainStatType = newStat) }
        updateMainStatCurve()
        checkMainStatConflict()
        updateAllSubStatsAvailableTypes()
    }

    private fun updateMainStatCurve() {
        val rarity = _state.value.rarity.stars
        val type = _state.value.mainStatType ?: return
        viewModelScope.launch {
            currentMainStatCurve = artifactRepository.getArtifactMainStatCurve(rarity, type)
            recalculateMainStatValue()
        }
    }

    private fun recalculateMainStatValue() {
        val value = calculateMainStatUseCase(_state.value.level, currentMainStatCurve)
        _state.update { it.copy(mainStatValue = value) }
    }

    private fun checkMainStatConflict() {
        val main = _state.value.mainStatType ?: return
        _state.update { state ->
            val newSubStats = state.subStats.map { if (it.type == main) it.copy(type = null, rollHistory = emptyList()) else it }
            state.copy(subStats = newSubStats)
        }
    }

    private fun updateAllSubStatsAvailableTypes() {
        _state.update { state ->
            val updatedSubStats = state.subStats.map { subStat ->
                subStat.copy(availableTypes = getAvailableSubStatsTypes(excludeId = subStat.id))
            }
            state.copy(subStats = updatedSubStats)
        }
    }

    private fun getAvailableSubStatsTypes(excludeId: Long): List<StatType> {
        val mainStat = _state.value.mainStatType
        val otherSelectedTypes = _state.value.subStats.filter { it.id != excludeId && it.type != null }.map { it.type }
        val allSubStats = listOf(StatType.HP, StatType.HP_PERCENT, StatType.ATK, StatType.ATK_PERCENT, StatType.DEF, StatType.DEF_PERCENT, StatType.ELEMENTAL_MASTERY, StatType.ENERGY_RECHARGE, StatType.CRIT_RATE, StatType.CRIT_DMG)
        return allSubStats.filter { it != mainStat && it !in otherSelectedTypes }
    }

    private fun refreshAllSubStatValues() {
        val rarity = _state.value.rarity.stars
        val currentSubStats = _state.value.subStats
        if (currentSubStats.isEmpty()) return
        viewModelScope.launch {
            val updatedList = currentSubStats.map { subStat ->
                if (subStat.type != null) {
                    val tiers = artifactRepository.getArtifactSubStatRolls(rarity, subStat.type) ?: emptyList()
                    subStat.copy(tierValues = tiers)
                } else subStat
            }
            _state.update { it.copy(subStats = updatedList) }
        }
    }

    fun onSaveClicked() {
        val validation = validateArtifactUseCase(_state.value)

        when (validation) {
            is ValidateArtifactUseCase.ValidationResult.Error -> {
                _state.update { it.copy(validationError = validation.messages) }
            }
            is ValidateArtifactUseCase.ValidationResult.Success -> {
                saveArtifactToDb()
            }
        }
    }

    fun onDismissError() {
        _state.update { it.copy(validationError = null) }
    }

    private fun saveArtifactToDb() {
        viewModelScope.launch {
            val s = _state.value

            val artifactPieceName = s.selectedSet?.pieces?.find { it.slot == s.slot }?.name ?: "Unknown Piece"

            val artifact = Artifact(
                id = 0,
                slot = s.slot,
                rarity = s.rarity,
                setName = s.selectedSet!!.name,
                artifactName = artifactPieceName,
                level = s.level,

                mainStat = Stat(
                    type = s.mainStatType!!,
                    value = if (s.mainStatType.isPercentage) {
                        StatValue.DoubleValue(s.mainStatValue.toDouble())
                    } else {
                        StatValue.IntValue(s.mainStatValue.toInt())
                    }
                ),

                // --- ИСПРАВЛЕНИЕ ДЛЯ SUB STATS ---
                subStats = s.subStats.filter { it.type != null }.map { sub ->
                    val type = sub.type!!
                    Stat(
                        type = type,
                        value = if (type.isPercentage) {
                            StatValue.DoubleValue(sub.value.toDouble())
                        } else {
                            StatValue.IntValue(sub.value.toInt())
                        }
                    )
                },

                isLocked = false
            )

            artifactRepository.addArtifact(artifact)
            _state.update { it.copy(isSaveSuccess = true) }
        }
    }
}