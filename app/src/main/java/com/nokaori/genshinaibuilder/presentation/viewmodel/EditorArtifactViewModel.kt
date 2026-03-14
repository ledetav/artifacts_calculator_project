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
import com.nokaori.genshinaibuilder.presentation.util.ScanSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle 
import com.nokaori.genshinaibuilder.domain.util.ParsedArtifactData

@HiltViewModel
class EditorArtifactViewModel @Inject constructor(
    private val artifactRepository: ArtifactRepository,
    private val calculateMainStatUseCase: CalculateArtifactMainStatUseCase,
    private val calculateSubStatRollsUseCase: CalculateSubStatRollsUseCase,
    private val validateArtifactUseCase: ValidateArtifactUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(EditorArtifactState())
    val state: StateFlow<EditorArtifactState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<EditorUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _allSets = artifactRepository.getAvailableArtifactSets()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredSets: StateFlow<List<ArtifactSet>> = combine(_allSets, _state) { sets, state ->
        if (state.setSearchQuery.isBlank()) sets
        else sets.filter { it.name.contains(state.setSearchQuery, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var currentMainStatCurve: StatCurve? = null

    init {
        // Проверяем, есть ли пакет артефактов из сканера
        val batch = ScanSessionManager.getBatchAndClear()
        if (batch.isNotEmpty()) {
            initBatch(batch)
        } else {
            // Обычная инициализация для одиночного артефакта
            val argId = savedStateHandle.get<String>("artifactId")?.toIntOrNull()
            if (argId != null && argId != -1) {
                loadArtifact(argId)
            } else {
                updateMainStatsForCurrentSlot()
            }
        }

        viewModelScope.launch {
            _state.collect { currentState ->
                val validation = validateArtifactUseCase(currentState)
                val newErrors = when (validation) {
                    is ValidateArtifactUseCase.ValidationResult.Error -> validation.messages
                    is ValidateArtifactUseCase.ValidationResult.Success -> emptyList()
                }

                if (currentState.validationErrors != newErrors) {
                    _state.update { it.copy(validationErrors = newErrors) }
                }
            }
        }
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
        
        _state.update { state -> 
            val newMaxSubStats = getMaxSubStatsFor(rarity, newLevel)
            val newSubStats = state.subStats.take(newMaxSubStats)
    
            state.copy(
                rarity = rarity, 
                maxLevel = newMaxLevel, 
                level = newLevel,
                maxSubStatsCount = newMaxSubStats,
                subStats = newSubStats
            ) 
        }
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
        _state.update { state -> 
            val newMaxSubStats = getMaxSubStatsFor(state.rarity, level)
            val newSubStats = state.subStats.take(newMaxSubStats)
    
            state.copy(
                level = level,
                maxSubStatsCount = newMaxSubStats,
                subStats = newSubStats
            ) 
        }
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

    private fun loadArtifact(id: Int) {
        viewModelScope.launch {
            val artifact = artifactRepository.getArtifactById(id) ?: return@launch
            
            val allSets = artifactRepository.getAvailableArtifactSets().first()
            val setInfo = allSets.find { it.name == artifact.setName }
            val fullSet = setInfo?.let { 
                try { artifactRepository.getArtifactSetDetails(it.id) } catch (e: Exception) { null } 
            }

            val newRarities = fullSet?.rarities?.sortedBy { it.stars } ?: listOf(artifact.rarity)
            val newMaxLevel = when(artifact.rarity) { 
                Rarity.FIVE_STARS -> 20; Rarity.FOUR_STARS -> 16; Rarity.THREE_STARS -> 12; else -> 4 
            }
            
            val loadedSubStats = artifact.subStats.mapIndexed { index, stat ->
                val tiers = artifactRepository.getArtifactSubStatRolls(artifact.rarity.stars, stat.type) ?: emptyList()
                val valueFloat = when (val v = stat.value) {
                    is StatValue.DoubleValue -> v.value.toFloat()
                    is StatValue.IntValue -> v.value.toFloat()
                }
                val rolls = calculateSubStatRollsUseCase(valueFloat, tiers) ?: listOf(valueFloat)
                
                SubStatState(
                    id = System.nanoTime() + index,
                    type = stat.type,
                    rollHistory = rolls,
                    tierValues = tiers
                )
            }

            val newMaxSubStats = getMaxSubStatsFor(artifact.rarity, artifact.level)

            _state.update {
                it.copy(
                    artifactId = artifact.id,
                    selectedSet = fullSet,
                    availableRarities = newRarities,
                    rarity = artifact.rarity,
                    slot = artifact.slot,
                    currentPieceIconUrl = getIconUrlForCurrentSelection(fullSet, artifact.slot),
                    level = artifact.level,
                    maxLevel = newMaxLevel,
                    mainStatType = artifact.mainStat.type,
                    mainStatValue = when(val v = artifact.mainStat.value) {
                        is StatValue.DoubleValue -> v.value.toFloat()
                        is StatValue.IntValue -> v.value.toFloat()
                    },
                    maxSubStatsCount = newMaxSubStats,
                    subStats = loadedSubStats.take(newMaxSubStats),
                    availableMainStats = ArtifactRules.getAllowedMainStats(artifact.slot)
                )
            }
            
            currentMainStatCurve = artifactRepository.getArtifactMainStatCurve(artifact.rarity.stars, artifact.mainStat.type)
            updateAllSubStatsAvailableTypes()
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
        if (_state.value.validationErrors.isEmpty()) {
            saveArtifactToDb()
        }
    }

    fun onBiometricSaveClicked() {
        if (_state.value.validationErrors.isEmpty()) {
            _state.update { it.copy(showBiometricPrompt = true) }
        }
    }

    fun onBiometricSuccess() {
        _state.update { it.copy(showBiometricPrompt = false) }
        saveArtifactToDb()
    }

    fun onBiometricErrorOrCancel() {
        _state.update { it.copy(showBiometricPrompt = false) }
    }

    fun onDoubleTapTriggered() {
        if (_state.value.validationErrors.isEmpty()) {
            saveArtifactToDb()
        }
    }

    private fun saveArtifactToDb() {
        viewModelScope.launch {
            val s = _state.value

            val artifactPieceName = s.selectedSet?.pieces?.find { it.slot == s.slot }?.name ?: "Unknown Piece"

            val artifact = Artifact(
                id = s.artifactId ?: 0,
                slot = s.slot,
                rarity = s.rarity,
                setName = s.selectedSet!!.name,
                artifactName = artifactPieceName,
                iconUrl = s.currentPieceIconUrl ?: "",
                level = s.level,

                mainStat = Stat(
                    type = s.mainStatType!!,
                    value = if (s.mainStatType.isPercentage) {
                        StatValue.DoubleValue(s.mainStatValue.toDouble())
                    } else {
                        StatValue.IntValue(s.mainStatValue.toInt())
                    }
                ),

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

            if (s.artifactId != null && s.artifactId != 0) {
                artifactRepository.updateArtifact(artifact)
            } else {
                artifactRepository.addArtifact(artifact)
            }
            
            _state.update { it.copy(isSaveSuccess = true) }
        }
    }

    private fun getMaxSubStatsFor(rarity: Rarity, level: Int): Int {
        val maxInitial = when (rarity) {
            Rarity.FIVE_STARS -> 4
            Rarity.FOUR_STARS -> 3
            Rarity.THREE_STARS -> 2
            else -> 0
        }
        return minOf(4, maxInitial + (level / 4))
    }

    fun applyScannedData(data: ParsedArtifactData) {
        if (data.slot != null) {
            onSlotChanged(data.slot)
        }
        if (data.level != null) {
            onLevelChanged(data.level)
            if (data.level > 16) {
                onRarityChanged(Rarity.FIVE_STARS)
            }
        }
        if (data.mainStatType != null) {
            onMainStatTypeChanged(data.mainStatType)
        }
        
        if (data.setId != null || data.setName != null) {
            viewModelScope.launch {
                val allSets = _allSets.value.ifEmpty { artifactRepository.getAvailableArtifactSets().first() }
                
                val matchedSet = if (data.setId != null) {
                    allSets.find { it.id == data.setId }
                } else {
                    allSets.find { it.name.equals(data.setName, ignoreCase = true) }
                }
                
                if (matchedSet != null) {
                    onSetSelected(matchedSet)
                }
            }
        }
        
        data.subStats.forEach { (statType, value) ->
            onAddSubStat()
            val lastSubStat = _state.value.subStats.lastOrNull() ?: return@forEach
            onSubStatTypeChanged(lastSubStat.id, statType)
            onSubStatManualValueEntered(lastSubStat.id, value.toString())
        }
    }

    fun initBatch(artifacts: List<ParsedArtifactData>) {
        if (artifacts.isEmpty()) return
        
        _state.update { 
            it.copy(
                artifactsBatch = artifacts,
                currentBatchIndex = 0
            )
        }
        loadArtifactIntoEditor(artifacts.first())
    }

    fun moveToNextInBatch() {
        val state = _state.value
        if (!state.isBatchMode || state.isLastInBatch) return
        
        val nextIndex = state.currentBatchIndex + 1
        _state.update { it.copy(currentBatchIndex = nextIndex) }
        loadArtifactIntoEditor(state.artifactsBatch[nextIndex])
    }

    fun moveToPreviousInBatch() {
        val state = _state.value
        if (state.currentBatchIndex <= 0) return
        
        val prevIndex = state.currentBatchIndex - 1
        _state.update { it.copy(currentBatchIndex = prevIndex) }
        loadArtifactIntoEditor(state.artifactsBatch[prevIndex])
    }

    private fun loadArtifactIntoEditor(artifactData: ParsedArtifactData) {
        _state.update { it.copy(
            artifactId = null,
            selectedSet = null,
            slot = artifactData.slot ?: ArtifactSlot.FLOWER_OF_LIFE,
            level = artifactData.level ?: 0,
            mainStatType = artifactData.mainStatType,
            mainStatValue = artifactData.mainStatValue ?: 0f,
            subStats = emptyList()
        )}
        
        if (artifactData.slot != null) {
            onSlotChanged(artifactData.slot)
        }
        if (artifactData.level != null) {
            onLevelChanged(artifactData.level)
            if (artifactData.level > 16) {
                onRarityChanged(Rarity.FIVE_STARS)
            }
        }
        if (artifactData.mainStatType != null) {
            onMainStatTypeChanged(artifactData.mainStatType)
        }
        
        if (artifactData.setId != null || artifactData.setName != null) {
            viewModelScope.launch {
                val allSets = _allSets.value.ifEmpty { artifactRepository.getAvailableArtifactSets().first() }
                
                val matchedSet = if (artifactData.setId != null) {
                    allSets.find { it.id == artifactData.setId }
                } else {
                    allSets.find { it.name.equals(artifactData.setName, ignoreCase = true) }
                }
                
                if (matchedSet != null) {
                    onSetSelected(matchedSet)
                }
            }
        }
        
        artifactData.subStats.forEach { (statType, value) ->
            onAddSubStat()
            val lastSubStat = _state.value.subStats.lastOrNull() ?: return@forEach
            onSubStatTypeChanged(lastSubStat.id, statType)
            onSubStatManualValueEntered(lastSubStat.id, value.toString())
        }
    }

    fun saveCurrentAndNext() {
        if (_state.value.validationErrors.isNotEmpty()) return
        
        viewModelScope.launch {
            saveArtifactToDb()
            moveToNextOrFinish()
        }
    }

    fun skipCurrentAndNext() {
        moveToNextOrFinish()
    }

    private fun moveToNextOrFinish() {
        val currentState = _state.value
        val nextIndex = currentState.currentBatchIndex + 1

        if (nextIndex < currentState.artifactsBatch.size) {
            val nextArtifact = currentState.artifactsBatch[nextIndex]
            _state.update { it.copy(currentBatchIndex = nextIndex) }
            loadArtifactIntoEditor(nextArtifact)
        } else {
            finishEditing()
        }
    }

    private fun finishEditing() {
        viewModelScope.launch {
            _uiEvent.emit(EditorUiEvent.BatchCompleted)
        }
    }
}
