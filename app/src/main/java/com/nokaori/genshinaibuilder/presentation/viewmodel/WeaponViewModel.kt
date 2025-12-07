package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import com.nokaori.genshinaibuilder.presentation.ui.weapons.data.WeaponFilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.nokaori.genshinaibuilder.domain.usecase.FilterWeaponsUseCase

class WeaponViewModel(
    private val weaponRepository: WeaponRepository,
    private val filterWeaponsUseCase: FilterWeaponsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _weaponFilterState = MutableStateFlow(WeaponFilterState())
    val weaponFilterState: StateFlow<WeaponFilterState> = _weaponFilterState.asStateFlow()

    private val _isFilterDialogShown = MutableStateFlow(false)
    val isFilterDialogShown: StateFlow<Boolean> = _isFilterDialogShown.asStateFlow()

    private val _draftWeaponFilterState = MutableStateFlow(_weaponFilterState.value)
    val draftWeaponFilterState: StateFlow<WeaponFilterState> = _draftWeaponFilterState.asStateFlow()

    val areWeaponFiltersChanged: StateFlow<Boolean> =
        combine(draftWeaponFilterState, weaponFilterState) { draft, current ->
            draft != current
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

   val searchedWeapons: StateFlow<List<UserWeapon>> = combine(
        weaponRepository.getUserWeapons(),
        _searchQuery,
        _weaponFilterState
    ) { weapons, query, filterState ->
        filterWeaponsUseCase(
            weapons = weapons,
            searchQuery = query,
            selectedWeaponTypes = filterState.selectedWeaponTypes,
            levelRange = filterState.levelRange,
            selectedMainStat = filterState.selectedMainStat
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterIconClicked() {
        _draftWeaponFilterState.value = _weaponFilterState.value
        _isFilterDialogShown.value = true
    }

    fun onFilterDialogDismiss() {
        _isFilterDialogShown.value = false
    }

    fun onApplyFilters() {
        _weaponFilterState.value = _draftWeaponFilterState.value
        _isFilterDialogShown.value = false
    }

    fun onResetFilters() {
        _draftWeaponFilterState.value = WeaponFilterState()
    }

    fun onWeaponTypeSelected(weaponType: WeaponType) {
        _draftWeaponFilterState.update { current ->
            val newSelectedTypes = if (current.selectedWeaponTypes.contains(weaponType)) {
                current.selectedWeaponTypes - weaponType
            } else {
                current.selectedWeaponTypes + weaponType
            }
            current.copy(selectedWeaponTypes = newSelectedTypes)
        }
    }

    fun onWeaponLevelRangeChanged(range: ClosedFloatingPointRange<Float>) {
        _draftWeaponFilterState.update { it.copy(levelRange = range) }
    }

    fun onWeaponMainStatSelected(statType: com.nokaori.genshinaibuilder.domain.model.StatType) {
        _draftWeaponFilterState.update { it.copy(selectedMainStat = statType) }
    }

    fun onClearWeaponMainStat() {
        _draftWeaponFilterState.update { it.copy(selectedMainStat = null) }
    }

    fun onWeaponLevelManualInput(fromText: String, toText: String) {
        val from = fromText.toIntOrNull()?.coerceIn(0, 90) ?: 0
        val to = toText.toIntOrNull()?.coerceIn(0, 90) ?: 90
        if (from <= to) {
            _draftWeaponFilterState.update { it.copy(levelRange = from.toFloat()..to.toFloat()) }
        }
    }

    fun addDefaultWeapon() {
        viewModelScope.launch {
            val allWeapons = weaponRepository.getAllWeapons().first()

            if (allWeapons.isNotEmpty()) {
                val randomBaseWeapon = allWeapons.random()
                val newUserWeapon = UserWeapon(
                    id = 0,
                    weapon = randomBaseWeapon,
                    level = 90,
                    ascension = 6,
                    refinement = 1
                )
                weaponRepository.addUserWeapon(newUserWeapon)
            }
        }
    }
}
