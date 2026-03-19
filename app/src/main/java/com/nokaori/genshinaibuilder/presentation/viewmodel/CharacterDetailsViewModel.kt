package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.*
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import com.nokaori.genshinaibuilder.domain.repository.SettingsRepository
import com.nokaori.genshinaibuilder.domain.usecase.CalculateCharacterStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val characterRepository: CharacterRepository,
    private val weaponRepository: WeaponRepository,
    private val artifactRepository: ArtifactRepository,
    private val calculateStatsUseCase: CalculateCharacterStatsUseCase,
    private val themeRepository: SettingsRepository
) : ViewModel() {

    private val characterId: Int = checkNotNull(savedStateHandle["characterId"])

    private val _character = MutableStateFlow<Character?>(null)
    val character: StateFlow<Character?> = _character.asStateFlow()

    val userCharacter: StateFlow<UserCharacter?> = characterRepository.getUserCharacter(characterId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val equippedWeapon: StateFlow<List<UserWeapon>> = weaponRepository.getUserWeapons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val talents: StateFlow<List<CharacterTalent>> = themeRepository.appLanguage
        .flatMapLatest { _ ->
            characterRepository.getTalents(characterId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val constellations: StateFlow<List<CharacterConstellation>> = themeRepository.appLanguage
        .flatMapLatest { _ ->
            characterRepository.getConstellations(characterId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _promotions = MutableStateFlow<List<CharacterPromotion>>(emptyList())
    private val _curve = MutableStateFlow<StatCurve?>(null)

    val characterStats: StateFlow<CharacterStatsResult?> = combine(
        _character,
        userCharacter,
        _curve,
        _promotions
    ) { char, userChar, curve, promos ->
        if (char == null) return@combine null

        calculateStatsUseCase(
            character = char,
            userCharacter = userChar,
            curve = curve,
            promotions = promos
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    init {
        loadCharacterInfo()
    }

    private fun loadCharacterInfo() {
        viewModelScope.launch {
            val char = characterRepository.getCharacterById(characterId)
            _character.value = char

            if (char != null) {
                launch { _promotions.value = characterRepository.getCharacterPromotions(characterId) }
                launch { _curve.value = characterRepository.getStatCurve(char.curveId) }
            }
        }
    }

    fun toggleOwnership() {
        viewModelScope.launch {
            characterRepository.toggleCharacterOwnership(characterId)
        }
    }
}