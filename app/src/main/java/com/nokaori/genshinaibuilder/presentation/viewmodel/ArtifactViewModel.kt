package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.usecase.FilterArtifactsUseCase
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.data.ArtifactFilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class ArtifactViewModel(
    private val artifactRepository: ArtifactRepository,
    private val filterArtifactsUseCase: FilterArtifactsUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isFilterDialogShown = MutableStateFlow(false)
    val isFilterDialogShown : StateFlow<Boolean> = _isFilterDialogShown.asStateFlow()

    private val _activeArtifactFilterState = MutableStateFlow(ArtifactFilterState())
    private val _draftArtifactFilterState = MutableStateFlow(ArtifactFilterState())
    val draftArtifactFilterState: StateFlow<ArtifactFilterState> = _draftArtifactFilterState.asStateFlow()

    val areArtifactFiltersChanged: StateFlow<Boolean> = combine(
        _activeArtifactFilterState,
        draftArtifactFilterState
    ) { active, draft ->
        active != draft
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Данные берутся из БД через репозиторий
    val availableArtifactSets : StateFlow<List<ArtifactSet>> = artifactRepository.getAvailableArtifactSets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredArtifactSets: StateFlow<List<ArtifactSet>> = combine(
        availableArtifactSets,
        draftArtifactFilterState
    ){ allArtifactSets, filters ->
        val query = filters.artifactSetSearchQuery
        if(query.isBlank()) allArtifactSets
        else allArtifactSets.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Список артефактов
    val searchedArtifacts: StateFlow<List<Artifact>> = combine(
        artifactRepository.getArtifacts(),
        _searchQuery,
        _activeArtifactFilterState
    ) { artifacts, query, filters ->
        filterArtifactsUseCase(
            artifacts,
            query,
            filters.selectedArtifactSet,
            filters.selectedArtifactLevelRange,
            filters.selectedArtifactSlots,
            filters.selectedArtifactMainStat
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- UI Events ---

    fun onSearchQueryChange(newQuery: String){ _searchQuery.value = newQuery }
    fun onFilterIconClicked(){
        _draftArtifactFilterState.value = _activeArtifactFilterState.value
        _isFilterDialogShown.value = true
    }
    fun onFilterDialogDismiss(){ _isFilterDialogShown.value = false }

    fun onApplyFilters(){
        _activeArtifactFilterState.value = _draftArtifactFilterState.value
        _isFilterDialogShown.value = false
    }

    fun onResetFilters(){
        val defaultState = ArtifactFilterState()
        _activeArtifactFilterState.value = defaultState
        _draftArtifactFilterState.value = defaultState
        _isFilterDialogShown.value = false
    }

    fun onArtifactSetSelected(artifactSet: ArtifactSet){
        _draftArtifactFilterState.update {
            it.copy(selectedArtifactSet = artifactSet, artifactSetSearchQuery = artifactSet.name, isArtifactSetDropdownExpanded = false)
        }
    }

    fun onArtifactSetSearchQueryChanged(newQuery: String){
        _draftArtifactFilterState.update {
            it.copy(artifactSetSearchQuery = newQuery, selectedArtifactSet = null, isArtifactSetDropdownExpanded = true)
        }
    }

    fun onArtifactSetFilterDropdownExpandedChanged(isExpanded: Boolean) {
        _draftArtifactFilterState.update { it.copy(isArtifactSetDropdownExpanded = isExpanded) }
    }

    fun onClearSelectedArtifactSet(){
        _draftArtifactFilterState.update { it.copy(selectedArtifactSet = null, artifactSetSearchQuery = "") }
    }

    fun onLevelRangeChanged(newRange: ClosedFloatingPointRange<Float>) {
        val start = newRange.start.roundToInt().toFloat()
        val end = newRange.endInclusive.roundToInt().toFloat()
        _draftArtifactFilterState.update { it.copy(selectedArtifactLevelRange = start..end) }
    }

    fun onLevelManualInputChanged(from: String, to: String) {
        val current = _draftArtifactFilterState.value.selectedArtifactLevelRange
        val f = (from.toIntOrNull() ?: current.start.toInt()).coerceIn(0, 20)
        val t = (to.toIntOrNull() ?: current.endInclusive.toInt()).coerceIn(0, 20)
        _draftArtifactFilterState.update {
            it.copy(selectedArtifactLevelRange = min(f, t).toFloat()..max(f, t).toFloat())
        }
    }

    fun onArtifactSlotClicked(slot: ArtifactSlot) {
        _draftArtifactFilterState.update { s ->
            val slots = s.selectedArtifactSlots.toMutableSet()
            if (!slots.remove(slot)) slots.add(slot)
            s.copy(selectedArtifactSlots = slots)
        }
    }

    fun onArtifactMainStatSelected(statType: StatType) {
        _draftArtifactFilterState.update { it.copy(selectedArtifactMainStat = statType) }
    }

    fun onClearSelectedArtifactMainStat() {
        _draftArtifactFilterState.update { it.copy(selectedArtifactMainStat = null) }
    }
}