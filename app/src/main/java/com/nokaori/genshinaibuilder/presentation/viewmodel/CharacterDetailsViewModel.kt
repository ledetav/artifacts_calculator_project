package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.*
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val characterRepository: CharacterRepository,
    private val weaponRepository: WeaponRepository,
    private val artifactRepository: ArtifactRepository
) : ViewModel() {

    private val characterId: Int = checkNotNull(savedStateHandle["characterId"])

    private val _character = MutableStateFlow<Character?>(null)
    val character: StateFlow<Character?> = _character.asStateFlow()

    val userCharacter: StateFlow<UserCharacter?> = characterRepository.getUserCharacter(characterId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val equippedWeapon: StateFlow<List<UserWeapon>> = weaponRepository.getUserWeapons()
        .map { list -> list.filter { it.equippedCharacterId == userCharacter.value?.characterId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val talents: StateFlow<List<CharacterTalent>> = characterRepository.getTalents(characterId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val constellations: StateFlow<List<CharacterConstellation>> = characterRepository.getConstellations(characterId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadCharacterInfo()
    }

    private fun loadCharacterInfo() {
        viewModelScope.launch {
            _character.value = characterRepository.getCharacterById(characterId)
        }
    }

    fun toggleOwnership() {
        viewModelScope.launch {
            characterRepository.toggleCharacterOwnership(characterId)
            _character.value = characterRepository.getCharacterById(characterId)
        }
    }
}