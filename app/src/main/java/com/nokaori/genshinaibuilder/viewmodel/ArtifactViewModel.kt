package com.nokaori.genshinaibuilder.viewmodel

import androidx.lifecycle.ViewModel
import com.nokaori.genshinaibuilder.data.Artifact
import com.nokaori.genshinaibuilder.data.ArtifactRarity
import com.nokaori.genshinaibuilder.data.ArtifactSlot
import com.nokaori.genshinaibuilder.data.ArtifactStat
import com.nokaori.genshinaibuilder.data.StatType
import com.nokaori.genshinaibuilder.data.StatValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArtifactViewModel : ViewModel() {
    private val _artifacts = MutableStateFlow<List<Artifact>>(emptyList())

    val artifacts: StateFlow<List<Artifact>> = _artifacts.asStateFlow()

    fun addDefaultaArtifact() {
        val newArtifact = Artifact(
            slot = ArtifactSlot.SANDS_OF_EON,
            rarity = ArtifactRarity.FIVE_STARS,
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
        _artifacts.value += newArtifact
    }
}