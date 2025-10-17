package com.nokaori.genshinaibuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.data.Artifact
import com.nokaori.genshinaibuilder.data.ArtifactRarity
import com.nokaori.genshinaibuilder.data.ArtifactSet
import com.nokaori.genshinaibuilder.data.ArtifactSlot
import com.nokaori.genshinaibuilder.data.ArtifactStat
import com.nokaori.genshinaibuilder.data.StatType
import com.nokaori.genshinaibuilder.data.StatValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

class ArtifactViewModel : ViewModel() {
    private val _artifacts = MutableStateFlow<List<Artifact>>(emptyList())
    val artifacts: StateFlow<List<Artifact>> = _artifacts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isFilterDialogShown = MutableStateFlow(false)
    val isFilterDialogShown : StateFlow<Boolean> = _isFilterDialogShown.asStateFlow()

    private val _areFiltersChanged = MutableStateFlow(false)
    val areFiltersChanged : StateFlow<Boolean> = _areFiltersChanged.asStateFlow()

    private val _availableArtifactSets = MutableStateFlow<List<ArtifactSet>>(emptyList())
    val availableArtifactSets : StateFlow<List<ArtifactSet>> = _availableArtifactSets.asStateFlow()

    private val _selectedArtifactSet = MutableStateFlow<ArtifactSet?>(null)
    val selectedArtifactSet : StateFlow<ArtifactSet?> = _selectedArtifactSet.asStateFlow()

    private val _artifactSetSearchQuery = MutableStateFlow("")
    val artifactSetSearchQuery : StateFlow<String> = _artifactSetSearchQuery.asStateFlow()

    private val _isArtifactSetDropdownExpanded = MutableStateFlow(false)
    val isArtifactSetDropdownExpanded : StateFlow<Boolean> = _isArtifactSetDropdownExpanded.asStateFlow()

    val filteredArtifactSets: StateFlow<List<ArtifactSet>> = availableArtifactSets.combine(artifactSetSearchQuery) {
        allArtifactSets, query ->
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

    init {
        loadAvailableArtifactSets()
    }

    // Заглушка
    private fun loadAvailableArtifactSets() {
        _availableArtifactSets.value = listOf(
            ArtifactSet("Киноварное загробье"),
            ArtifactSet("Ночь открытого неба")
        )
    }

    fun onFilterIconClicked(){
        _isFilterDialogShown.value = true
    }

    fun onFilterDialogDismiss(){
        _isFilterDialogShown.value = false
    }

    fun onApplyFilters(){
        // Заглушка
        onFilterDialogDismiss()
    }

    fun onResetFilters(){
        _selectedArtifactSet.value = null
        _artifactSetSearchQuery.value = ""
        _areFiltersChanged.value = false
    }

    fun onArtifactSetSelected(artifactSet: ArtifactSet){
        _selectedArtifactSet.value = artifactSet
        _artifactSetSearchQuery.value = artifactSet.name
        _isArtifactSetDropdownExpanded.value = false
        _areFiltersChanged.value = true
    }

    fun onArtifactSetSearchQueryChanged(newQuery: String){
        _artifactSetSearchQuery.value = newQuery
        _isArtifactSetDropdownExpanded.value = true
        _selectedArtifactSet.value = null
    }

    fun onArtifactSetDropdownStateChanged(isExpanded: Boolean){
        _isArtifactSetDropdownExpanded.value = isExpanded
    }

    val searchedArtifacts: StateFlow<List<Artifact>> = combine(artifacts, searchQuery, selectedArtifactSet)
    {
        allArtifacts, searchQuery, selectedArtifactSet ->
        val searchedList = if (searchQuery.isBlank()) {
            allArtifacts
        } else {
            allArtifacts.filter { artifact ->
                artifact.setName.contains(searchQuery, ignoreCase = true) ||
                        artifact.artifactName.contains(searchQuery, ignoreCase = true)
            }
        }

        val filteredList = if(selectedArtifactSet == null){
            searchedList
        } else {
            searchedList.filter { artifact ->
                artifact.setName == selectedArtifactSet.name
            }
        }

        filteredList.sortedBy { artifact ->
            when {
                searchQuery.isNotBlank() &&
                        artifact.artifactName.contains(searchQuery, ignoreCase = true) -> 0
                else -> 1
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(newQuery: String){
        _searchQuery.value = newQuery
    }

    fun addDefaultaArtifact() {
        val randomArtifact = if(Random.nextBoolean()) {
            Artifact(
                slot = ArtifactSlot.SANDS_OF_EON,
                rarity = ArtifactRarity.FIVE_STARS,
                artifactName = "Солнечная реликвия",
                setName = "Киноварное загробье",
                level = 0,
                mainStat = ArtifactStat(StatType.DEF_PERCENT, StatValue.DoubleValue(8.7)),
                subStats = listOf(
                    ArtifactStat(StatType.ENERGY_RECHARGE, StatValue.DoubleValue(6.5)),
                    ArtifactStat(StatType.HP, StatValue.IntValue(239)),
                    ArtifactStat(StatType.ELEMENTAL_MASTERY, StatValue.IntValue(19)),
                    ArtifactStat(StatType.ATK, StatValue.IntValue(18))
                )
            )
        } else {
            Artifact(
                slot = ArtifactSlot.FLOWER_OF_LIFE,
                rarity = ArtifactRarity.FIVE_STARS,
                artifactName = "Цветок жажды познания",
                setName = "Ночь открытого неба",
                level = 20,
                mainStat = ArtifactStat(StatType.HP, StatValue.IntValue(4780)),
                subStats = listOf(
                    ArtifactStat(StatType.CRIT_RATE, StatValue.DoubleValue(8.6)),
                    ArtifactStat(StatType.CRIT_DMG, StatValue.DoubleValue(5.4)),
                    ArtifactStat(StatType.ENERGY_RECHARGE, StatValue.DoubleValue(11.0)),
                    ArtifactStat(StatType.HP_PERCENT, StatValue.DoubleValue(8.7))
                )
            )
        }
        _artifacts.value += randomArtifact
    }
}