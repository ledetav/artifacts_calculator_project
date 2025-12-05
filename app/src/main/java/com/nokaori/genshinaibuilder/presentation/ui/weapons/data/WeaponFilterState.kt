package com.nokaori.genshinaibuilder.presentation.ui.weapons.data

import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType

data class WeaponFilterState(
    val selectedWeaponTypes: Set<WeaponType> = emptySet(),
    val selectedMainStat: StatType? = null,
    val levelRange: ClosedFloatingPointRange<Float> = 0f..90f,
)