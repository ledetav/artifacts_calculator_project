package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WeaponViewModel(
    private val weaponRepository: WeaponRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchedWeapons: StateFlow<List<UserWeapon>> = weaponRepository.getUserWeapons()
        .combine(searchQuery) { weapons, query ->
            if (query.isBlank()) {
                weapons
            } else {
                weapons.filter { it.weapon.name.contains(query, ignoreCase = true) }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
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