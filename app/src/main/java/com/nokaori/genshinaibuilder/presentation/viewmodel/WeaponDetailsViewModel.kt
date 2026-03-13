package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeaponDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WeaponRepository,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val weaponId: Int = checkNotNull(savedStateHandle["weaponId"])

    private val _weapon = MutableStateFlow<Weapon?>(null)
    val weapon: StateFlow<Weapon?> = _weapon.asStateFlow()

    init {
        loadWeapon()
    }

    private fun loadWeapon() {
        viewModelScope.launch {
            try {
                val w = repository.getWeaponDetails(weaponId)
                _weapon.value = w
            } catch (e: Exception) {
                _weapon.value = null
            }
        }
    }
}
