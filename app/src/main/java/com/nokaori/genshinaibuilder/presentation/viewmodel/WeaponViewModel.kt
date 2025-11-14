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

class WeaponViewModel(
    private val weaponRepository: WeaponRepository
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


    val searchedWeapons: StateFlow<List<UserWeapon>> = weaponRepository.getUserWeapons()
        .combine(searchQuery) { weapons, query ->
            if (query.isBlank()) {
                weapons
            } else {
                weapons.filter { it.weapon.name.contains(query, ignoreCase = true) }
            }
        }
        .combine(weaponFilterState) { weapons, filterState ->
            weapons.filter { userWeapon ->
                (filterState.selectedWeaponTypes.isEmpty() || userWeapon.weapon.type in filterState.selectedWeaponTypes) &&
                userWeapon.level in filterState.levelRange.start.toInt()..filterState.levelRange.endInclusive.toInt()
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
