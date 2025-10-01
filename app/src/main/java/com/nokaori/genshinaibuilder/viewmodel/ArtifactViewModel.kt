package com.nokaori.genshinaibuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.data.Artifact
import com.nokaori.genshinaibuilder.data.ArtifactRarity
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

    val searchedArtifacts: StateFlow<List<Artifact>> = artifacts.combine(searchQuery) {
        allArtifacts, query ->
        if (query.isBlank()) {
            allArtifacts
        } else {
            allArtifacts.filter { artifact ->
                val matchesSetName = artifact.setName.contains(query, ignoreCase = true)
                val matchesArtifactName = artifact.artifactName.contains(query, ignoreCase = true)

                matchesSetName || matchesArtifactName
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