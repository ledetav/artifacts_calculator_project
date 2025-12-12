package com.nokaori.genshinaibuilder.presentation.ui.mappers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Style
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import com.nokaori.genshinaibuilder.domain.usecase.FilterCharactersUseCase

@Composable
fun ArtifactSlot.toDisplayName(): String {
    return when (this) {
        ArtifactSlot.FLOWER_OF_LIFE -> stringResource(R.string.artifact_slot_flower)
        ArtifactSlot.PLUME_OF_DEATH -> stringResource(R.string.artifact_slot_plume)
        ArtifactSlot.SANDS_OF_EON -> stringResource(R.string.artifact_slot_sands)
        ArtifactSlot.GOBLET_OF_EONOTHEM -> stringResource(R.string.artifact_slot_goblet)
        ArtifactSlot.CIRCLET_OF_LOGOS -> stringResource(R.string.artifact_slot_circlet)
    }
}

@Composable
fun WeaponType.toDisplayName(): String {
    return when (this) {
        WeaponType.SWORD -> stringResource(R.string.weapon_type_sword)
        WeaponType.CLAYMORE -> stringResource(R.string.weapon_type_claymore)
        WeaponType.POLEARM -> stringResource(R.string.weapon_type_polearm)
        WeaponType.BOW -> stringResource(R.string.weapon_type_bow)
        WeaponType.CATALYST -> stringResource(R.string.weapon_type_catalyst)
        else -> stringResource(R.string.weapon_type_unknown)
    }
}

@Composable
fun StatType.toDisplayName(showPercentSign: Boolean = true): String {
    val stringId = when (this) {
        StatType.ATK -> R.string.stat_type_atk
        StatType.DEF -> R.string.stat_type_def
        StatType.HP -> R.string.stat_type_hp
        StatType.ATK_PERCENT -> R.string.stat_type_atk
        StatType.DEF_PERCENT -> R.string.stat_type_def
        StatType.HP_PERCENT -> R.string.stat_type_hp
        StatType.CRIT_RATE -> R.string.stat_type_crit_rate
        StatType.CRIT_DMG -> R.string.stat_type_crit_dmg
        StatType.ENERGY_RECHARGE -> R.string.stat_type_energy_recharge
        StatType.ELEMENTAL_MASTERY -> R.string.stat_type_elemental_mastery
        StatType.ANEMO_DAMAGE_BONUS -> R.string.stat_type_anemo_damage_bonus
        StatType.GEO_DAMAGE_BONUS -> R.string.stat_type_geo_damage_bonus
        StatType.ELECTRO_DAMAGE_BONUS -> R.string.stat_type_electro_damage_bonus
        StatType.CRYO_DAMAGE_BONUS -> R.string.stat_type_cryo_damage_bonus
        StatType.DENDRO_DAMAGE_BONUS -> R.string.stat_type_dendro_damage_bonus
        StatType.HYDRO_DAMAGE_BONUS -> R.string.stat_type_hydro_damage_bonus
        StatType.PHYSICAL_DAMAGE_BONUS -> R.string.stat_type_physical_damage_bonus
        StatType.PYRO_DAMAGE_BONUS -> R.string.stat_type_pyro_damage_bonus
        StatType.HEALING_BONUS -> R.string.stat_type_healing_bonus
    }

    val baseName = stringResource(stringId)
    return if (showPercentSign && isPercentage) "$baseName %" else baseName
}

@Composable
fun getArtifactSetIcon(setName: String): ImageVector {
    return Icons.Default.Style
}

@Composable
fun FilterCharactersUseCase.OwnershipFilter.toDisplayName(): String {
    return when (this) {
        FilterCharactersUseCase.OwnershipFilter.ALL -> stringResource(R.string.filter_ownership_all)
        FilterCharactersUseCase.OwnershipFilter.ONLY_OWNED -> stringResource(R.string.filter_ownership_owned)
        FilterCharactersUseCase.OwnershipFilter.ONLY_MISSING -> stringResource(R.string.filter_ownership_missing)
    }
}