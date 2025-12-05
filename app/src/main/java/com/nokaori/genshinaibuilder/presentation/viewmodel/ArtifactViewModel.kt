package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.data.repository.ArtifactRepositoryImpl
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactRarity
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.StatValue
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
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

class ArtifactViewModel(
    private val artifactRepository: ArtifactRepository
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val availableArtifactSets : StateFlow<List<ArtifactSet>> = artifactRepository.getAvailableArtifactSets()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val filteredArtifactSets: StateFlow<List<ArtifactSet>> = combine(
        availableArtifactSets,
        draftArtifactFilterState
    ){ allArtifactSets, filters ->
        val query = filters.artifactSetSearchQuery

        if(query.isBlank()){
            allArtifactSets
        } else {
            allArtifactSets.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(newQuery: String){
        _searchQuery.value = newQuery
    }

    fun onFilterIconClicked(){
        _draftArtifactFilterState.value = _activeArtifactFilterState.value
        _isFilterDialogShown.value = true
    }

    fun onFilterDialogDismiss(){
        _isFilterDialogShown.value = false
    }

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
        _draftArtifactFilterState.update { currentState ->
            currentState.copy(
                selectedArtifactSet = artifactSet,
                artifactSetSearchQuery = artifactSet.name,
                isArtifactSetDropdownExpanded = false
            )
        }
    }

    fun onArtifactSetSearchQueryChanged(newQuery: String){
        _draftArtifactFilterState.update { currentState ->
            currentState.copy(
                artifactSetSearchQuery = newQuery,

                selectedArtifactSet = if (currentState.selectedArtifactSet?.name != newQuery) null
                    else currentState.selectedArtifactSet,

                isArtifactSetDropdownExpanded = true
            )
        }
    }

    fun onArtifactSetFilterDropdownExpandedChanged(isExpanded: Boolean) {
        _draftArtifactFilterState.update { it.copy(isArtifactSetDropdownExpanded = isExpanded) }
    }

    fun onClearSelectedArtifactSet(){
        _draftArtifactFilterState.update { it.copy(selectedArtifactSet = null, artifactSetSearchQuery = "") }
    }

    fun onLevelRangeChanged(newRange: ClosedFloatingPointRange<Float>) {
        val roundedStart = newRange.start.roundToInt().toFloat()
        val roundedEnd = newRange.endInclusive.roundToInt().toFloat()
        _draftArtifactFilterState.update { it.copy(selectedArtifactLevelRange = roundedStart..roundedEnd) }
    }

    fun onLevelManualInputChanged(from: String, to: String) {
        val currentRange = _draftArtifactFilterState.value.selectedArtifactLevelRange
        var fromInt = from.toIntOrNull() ?: currentRange.start.roundToInt()
        var toInt = to.toIntOrNull() ?: currentRange.endInclusive.roundToInt()

        fromInt = fromInt.coerceIn(0, 20)
        toInt = toInt.coerceIn(0, 20)

        val finalFrom = min(fromInt, toInt).toFloat()
        val finalTo = max(fromInt, toInt).toFloat()

        _draftArtifactFilterState.update { it.copy(selectedArtifactLevelRange = finalFrom..finalTo) }
    }

    fun onArtifactSlotClicked(slot: ArtifactSlot) {
        _draftArtifactFilterState.update { currentState ->
            val currentSlots = currentState.selectedArtifactSlots.toMutableSet()

            if (currentSlots.contains(slot)){
                currentSlots.remove(slot)
            } else {
                currentSlots.add(slot)
            }

            currentState.copy(selectedArtifactSlots = currentSlots)
        }
    }

    fun onArtifactMainStatSelected(statType: StatType) {
        _draftArtifactFilterState.update { it.copy(selectedArtifactMainStat = statType) }
    }

    fun onClearSelectedArtifactMainStat() {
        _draftArtifactFilterState.update { it.copy(selectedArtifactMainStat = null) }
    }

    fun addDefaultArtifact() {
        viewModelScope.launch {
            val randomArtifact = createRandomArtifact()
            artifactRepository.addArtifact(randomArtifact)
        }
    }

    private fun createRandomArtifact(): Artifact {
        if (Random.nextBoolean()) {
            return Artifact(
                slot = ArtifactSlot.SANDS_OF_EON,
                rarity = ArtifactRarity.FIVE_STARS,
                artifactName = "Солнечная реликвия",
                setName = "Киноварное загробье",
                level = 0,
                mainStat = Stat(StatType.DEF_PERCENT, StatValue.DoubleValue(8.7)),
                subStats = listOf(
                    Stat(StatType.ENERGY_RECHARGE, StatValue.DoubleValue(6.5)),
                    Stat(StatType.HP, StatValue.IntValue(239)),
                    Stat(StatType.ELEMENTAL_MASTERY, StatValue.IntValue(19)),
                    Stat(StatType.ATK, StatValue.IntValue(18))
                )
            )
        } else {
            return Artifact(
                slot = ArtifactSlot.FLOWER_OF_LIFE,
                rarity = ArtifactRarity.FIVE_STARS,
                artifactName = "Цветок жажды познания",
                setName = "Ночь открытого неба",
                level = 20,
                mainStat = Stat(StatType.HP, StatValue.IntValue(4780)),
                subStats = listOf(
                    Stat(StatType.CRIT_RATE, StatValue.DoubleValue(8.6)),
                    Stat(StatType.CRIT_DMG, StatValue.DoubleValue(5.4)),
                    Stat(StatType.ENERGY_RECHARGE, StatValue.DoubleValue(11.0)),
                    Stat(StatType.HP_PERCENT, StatValue.DoubleValue(8.7))
                )
            )
        }
    }
}